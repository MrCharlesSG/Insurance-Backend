package hr.algebra.insurancebackend.service;

public interface TokenBlackListService {
    void addToBlacklist(String token);
    boolean isBlacklisted(String token);
}
