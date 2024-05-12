package common.dto;

public record RequestUserStatsDto(Long countRequestByUser, Long countCompletedDealsByUser) {
}
