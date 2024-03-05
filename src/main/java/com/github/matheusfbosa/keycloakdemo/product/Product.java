package com.github.matheusfbosa.keycloakdemo.product;

import jakarta.validation.constraints.NotEmpty;

public record Product(Long id, @NotEmpty String title, String description) {

}
