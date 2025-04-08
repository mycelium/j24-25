package org.httpServerAyzek.http.handler;

import org.httpServerAyzek.http.HttpRes;
import org.httpServerAyzek.http.util.HttpReqParser;

public interface HttpHandler {
    void handle(HttpReqParser request, HttpRes response);
}
