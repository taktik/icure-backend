package org.taktik.icure.spring

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@ControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(InvalidFormatException::class)
    @ResponseBody
    fun typeMismatchException(request: HttpServletRequest?, servletResponse: HttpServletResponse?, e: InvalidFormatException?): String {
        return ""
    }
}
