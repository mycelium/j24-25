package ru.spbstu.hsai.httpserver.common;

public enum HttpStatus {
    Ok(200, "OK"),
    Created(201, "Created"),
    NoContent(204, "No Content"),

    BadRequest(400, "Bad Request"),
    NotFound(404, "Not Found"),

    InternalServerError(500, "Internal Server Error"),
    NotImplemented(501, "Not Implemented");

    private final int code;
    private final String info;

    HttpStatus(int code, String status) {
        this.code = code;
        this.info = status;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
