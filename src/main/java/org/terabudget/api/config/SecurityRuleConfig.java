package org.terabudget.api.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityRuleConfig {

    public static final List<String> EXACT_MATCHERS = List.of(
            "/actuator",
            "/api/auth/sign-in",
            "/api/auth/sign-up",
            "/api/auth/refresh-token",
            "/swagger-ui/");

    public static final List<String> START_WITH = List.of(
            "/actuator/",
            "/api-docs/",
            "/swagger-ui/");

    public static String[] globs;

    public static boolean matches(String url) {
        String lowerUrl = url.toLowerCase();
        if (EXACT_MATCHERS.contains(lowerUrl)) {
            return true;
        }
        boolean result = false;
        for (String part : START_WITH) {
            if (lowerUrl.startsWith(part)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String[] getGlobs() {
        if (globs == null) {
            synchronized (SecurityRuleConfig.class) {
                if (globs == null) {
                    List<String> resultList = new ArrayList<>(EXACT_MATCHERS);
                    List<String> globParts = START_WITH.stream()
                            .map(s -> s.concat("**"))
                            .collect(Collectors.toList());
                    resultList.addAll(globParts);
                    globs = resultList.toArray(new String[] {});
                }
            }
        }
        return globs;
    }
}
