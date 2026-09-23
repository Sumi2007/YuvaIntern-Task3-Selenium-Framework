package com.qapractice.framework.data;

/** One login attempt. {@code description} is shown in the report for data-driven runs. */
public record Credentials(String description, String email, String password) {
}
