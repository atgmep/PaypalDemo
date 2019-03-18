package nghia.firsttask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response<E> {

    //API Response code
    public static final int STATUS_SERVER_ERROR = -1;
    public static final int STATUS_FAIL = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_NO_RESULT = 2;

    public static final String MESSAGE_SUCCESS = "Success";
    public static final String MESSAGE_FAIL = "Fail";
    public static final String MESSAGE_SERVER_ERROR = "Server error";
    public static final String MESSAGE_NO_RESULT = "No result";

    @Expose
    @SerializedName(value = "status_code")
    private int statusCode;

    @Expose
    @SerializedName(value = "message")
    private String message;

    @Expose
    @SerializedName(value = "data")
    private E data;

    public Response(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public void setResponse(int statusCode, String message, E data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{statusCode=" + statusCode + ", message='" + message + "', data=" + data + "}";
    }
}
