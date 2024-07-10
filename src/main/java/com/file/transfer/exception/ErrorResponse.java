package com.file.transfer.exception;

import java.util.List;

public record ErrorResponse(String error, List<String> messages) {}
