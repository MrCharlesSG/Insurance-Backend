package hr.algebra.insurancebackend.security.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class InMemoryTokenBlacklist implements TokenBlackListService {
    private Set<String> blacklist = new HashSet<>();

    @Override
    public void addToBlacklist(String token) {
        blacklist.add(token);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    @Override
    public void removeFromBlackList(String token) {
        if(blacklist.contains(token)){
            removeFromBlackList(token);
        }
    }
}
