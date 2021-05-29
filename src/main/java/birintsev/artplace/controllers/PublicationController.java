package birintsev.artplace.controllers;

import birintsev.artplace.dto.PublicDTO;
import birintsev.artplace.dto.PublicationDTO;
import birintsev.artplace.dto.PublishRequestImpl;
import birintsev.artplace.dto.UserDTO;
import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.services.PublicService;
import birintsev.artplace.services.PublicationService;
import birintsev.artplace.services.exceptions.UnauthorizedOperationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/publication")
public class PublicationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PublicationController.class
    );

    private static final String BOUGHT_PUBLICATIONS_VIEW_NAME =
        "boughtPublications";

    private final PublicationService publicationService;

    private final ModelMapper modelMapper;

    private final PublicService publicService;

    @RequestMapping("/bought")
    protected ModelAndView bought(
        @AuthenticationPrincipal User user
    ) {
        ModelAndView mav = new ModelAndView(BOUGHT_PUBLICATIONS_VIEW_NAME);
        mav.addObject(
            "user",
            modelMapper.map(user, UserDTO.class)
        );
        mav.addObject(
            "publications",
            mapPublications(
                publicationService.findPermanentPublicationsByUserFirstPage(
                    user
                )
                    .stream()
                    .collect(Collectors.toList())
            )
        );
        return mav;
    }

    @RequestMapping("/bought/page")
    protected ResponseEntity<List<PublicationDTO>> boughtPage(
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        return new ResponseEntity<>(
            new ArrayList<>(
                mapPublications(
                    publicationService
                        .findPermanentPublicationsByUser(user, pageable)
                        .stream()
                        .collect(Collectors.toList())
                )
            ),
            HttpStatus.OK
        );
    }

    @RequestMapping(
        path = "/publish/{publicId}",
        method = RequestMethod.GET
    )
    protected ModelAndView publicationForm(
        @AuthenticationPrincipal User user,
        @PathVariable(name = "publicId") UUID publicId
    ) {
        Public parentPublic = publicService
            .findById(publicId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    String.format("Public (id = %s) not found", publicId)
                )
            );
        return new ModelAndView(
            "createPublication",
            Map.of(
                "parentPublic", modelMapper.map(parentPublic, PublicDTO.class)
            )
        );
    }

    @ExceptionHandler(value = {
        NoSuchElementException.class
    })
    protected ModelAndView onPublicationFormRequest(
        NoSuchElementException exception
    ) {
        LOGGER.error(exception.getMessage(), exception);
        return new ModelAndView(
            "redirect:/error",
            Map.of("message", "Public with provided id not found")
        );
    }

    @RequestMapping(
        path = "/publish",
        method = RequestMethod.POST
    )
    protected ResponseEntity<?> publish(
        @AuthenticationPrincipal User user,
        PublishRequestImpl publishRequest
    ) {
        Publication p = publicationService.publish(user, publishRequest);
        LOGGER.debug("New publication has been created: " + p);
        return ResponseEntity
            .ok()
            .body(
                modelMapper.map(
                    p,
                    PublicationDTO.class
                )
            );
    }

    @ExceptionHandler(value = {
        UnauthorizedOperationException.class
    })
    protected ResponseEntity<?> onPublicationCreation(
        UnauthorizedOperationException exception
    ) {
        LOGGER.error(exception.getMessage(), exception);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(new Object() {
                final String message = "The user is not the public owner";
            });
    }

    private Collection<PublicationDTO> mapPublications(
        Collection<Publication> publications
    ) {
        return modelMapper.map(
            publications,
            new TypeToken<List<PublicationDTO>>() {}.getType()
        );
    }
}
