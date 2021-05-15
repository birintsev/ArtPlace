package birintsev.artplace.controllers;

import birintsev.artplace.dto.PublicDTO;
import birintsev.artplace.dto.PublicationDTO;
import birintsev.artplace.dto.PublishRequestImpl;
import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.services.PublicService;
import birintsev.artplace.services.PublicationService;
import birintsev.artplace.services.exceptions.UnauthorizedOperationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/publication")
public class PublicationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PublicationController.class
    );

    private final PublicationService publicationService;

    private final ModelMapper modelMapper;

    private final PublicService publicService;

    @RequestMapping(
        path = "/publish/{publicId}",
        method = RequestMethod.GET
    )
    protected ModelAndView publicationForm(
        @AuthenticationPrincipal User user,
        @PathVariable(name = "publicId") UUID publicId) {
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
}
