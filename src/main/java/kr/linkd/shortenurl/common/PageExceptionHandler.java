package kr.linkd.shortenurl.common;

import kr.linkd.shortenurl.common.exception.LinkExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;


@ControllerAdvice
public class PageExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "404";
    }

    @ExceptionHandler(LinkExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public String handleLinkExpiredException() {
        return "expired";
    }

}
