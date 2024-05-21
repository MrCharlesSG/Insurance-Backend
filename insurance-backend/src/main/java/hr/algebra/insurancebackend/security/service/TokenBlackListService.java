package hr.algebra.insurancebackend.security.service;

public interface TokenBlackListService {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
