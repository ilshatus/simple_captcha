package captcha;

/**
 * Class which represents verification response for
 * request 'captcha/verify'
 */
public class VerificationResponse {
    private boolean success;
    private String errorCode;

    public VerificationResponse(boolean success, String errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
