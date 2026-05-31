package com.ark.base.ui

import com.ark.base.application.FileRequest
import com.ark.base.application.FileResponse
import com.ark.base.application.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @ModelAttribute request: FileRequest,
    ): FileResponse = fileService.upload(request)

    @GetMapping("/{fileId}")
    fun findById(
        @PathVariable fileId: Long,
    ): FileResponse = fileService.findById(fileId)

    @DeleteMapping("/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable fileId: Long,
    ) = fileService.delete(fileId)
}
