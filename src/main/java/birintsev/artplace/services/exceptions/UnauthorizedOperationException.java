package birintsev.artplace.services.exceptions;

import birintsev.artplace.model.db.User;

/**
 * Is thrown when a {@link birintsev.artplace.model.db.User}
 * lacks privileges on performing an operation
 * (e.g. creating a {@link birintsev.artplace.model.db.Publication}
 * in not owned {@link birintsev.artplace.model.db.Public})
 * */
public class UnauthorizedOperationException extends RuntimeException {

    private final User user;

    /**
     * @param message supplied message
     * */
    public UnauthorizedOperationException(String message) {
        super(message);
        user = null;
    }

    /**
     * @param message supplied message
     * @param user    a user who tried to perform an unauthorized action
     * */
    public UnauthorizedOperationException(String message, User user) {
        super(message);
        this.user = user;
    }

    @Override
    public String toString() {
        return "UnauthorizedOperationException{"
            + "user=" + user
            + ", "
            + "message="
            + getMessage()
            + '}';
    }
}
