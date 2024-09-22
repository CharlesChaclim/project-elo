package br.com.project.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Scope {
    public static final String ADMIN  ="hasAuthority('SCOPE_admin')";
    public static final String BASIC  ="hasAuthority('SCOPE_basic')";
}