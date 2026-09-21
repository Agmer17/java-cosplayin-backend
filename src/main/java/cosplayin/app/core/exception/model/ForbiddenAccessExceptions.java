package cosplayin.app.core.exception.model;

public class ForbiddenAccessExceptions extends RuntimeException {
    public ForbiddenAccessExceptions(String message) {
        super(message);
    }
}
