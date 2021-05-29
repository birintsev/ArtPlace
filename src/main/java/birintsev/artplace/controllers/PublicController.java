package birintsev.artplace.controllers;

import birintsev.artplace.dto.PublicDTO;
import birintsev.artplace.dto.PublicationDTO;
import birintsev.artplace.model.db.Public;
import birintsev.artplace.model.db.Publication;
import birintsev.artplace.model.db.User;
import birintsev.artplace.services.PublicService;
import birintsev.artplace.services.PublicationService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Controller for handling
 * {@link birintsev.artplace.model.db.Public Public}-related requests
 * */
@AllArgsConstructor
@RequestMapping("/public")
@Controller
public class PublicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PublicController.class
    );

    private final PublicService publicService;

    private final PublicationService publicationService;

    private final ModelMapper modelMapper;

    @RequestMapping(value = "/{publicId}", method = RequestMethod.GET)
    protected ModelAndView publicPage(
        @PathVariable("publicId") UUID publicId,
        HttpSession userSession
    ) {
        Public aPublic = publicService
            .findById(publicId)
            .orElseThrow(
                () -> new NoSuchElementException(
                    String.format("Public (id = %s) not found.", publicId)
                )
            );

        /*
        * TODO: add publications from UserPermanentPublication
        *       for the case if user visits not subscribed public
        * */
        Iterable<PublicationDTO> publications = mapPublications(
            publicationService.findByPublicFirstPage(aPublic)
        );
        return new ModelAndView(
            "publicPage",
            Map.of(
                "public",
                    modelMapper.map(aPublic, PublicDTO.class),
                "publications",
                    publications,
                "subscribersAmount",
                    publicService.getTotalSubscribersAmount(aPublic),
                "publicationsAmount",
                    publicationService.getTotalPublicationsCount(aPublic),
                "pageSize",
                    10
            ),
            HttpStatus.OK
        );
    }

    @RequestMapping(
        value = "/unsubscribe/{publicId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    protected ResponseEntity<?> unsubscribe(
        @AuthenticationPrincipal User user,
        @PathVariable("publicId") Public publicToUnsubscribe
    ) {
        if (publicToUnsubscribe == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Object() {
                    public final String message = "Public not found";
                });
        }

        if (!publicService.isSubscriber(user, publicToUnsubscribe)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Object() {
                    public final String message = String.format(
                        "The user (id = %s) is not a subscriber"
                            + " of the public (id = %s).",
                        user.getId(),
                        publicToUnsubscribe.getId()
                    );
                });
        }

        publicService.unsubscribe(user, publicToUnsubscribe);
        return ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new Object() {
                public final String message = String.format(
                    "The user (id = %s) has been unsubscribed"
                        + " from the public (id = %s).",
                    user.getId(),
                    publicToUnsubscribe.getId()
                );
            });
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ModelAndView onGetPublicPage(NoSuchElementException exception) {
        LOGGER.error(exception.getMessage(), exception);
        return new ModelAndView(
            "redirect:/error",
            Map.of("message", "Public with such id does not exist.")
        );
    }

    private Iterable<PublicationDTO> mapPublications(
        Iterable<Publication> publications
    ) {

        return StreamSupport.stream(publications.spliterator(), false)
            .map(p -> modelMapper.map(p, PublicationDTO.class))
            .collect(Collectors.toList());
    }
}
