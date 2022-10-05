package lotto.domain;

import lotto.controller.LottoFactory;
import lotto.domain.enums.Rank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Lottos {
    private final List<Lotto> lottos;

    public Lottos(LottoFactory lottoFactory, Integer count) {
        this.lottos = IntStream.range(0, count)
                .mapToObj(i -> lottoFactory.lotto())
                .collect(toList());
    }

    public Lottos(List<Lotto> lottos) {
        this.lottos = lottos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lottos lottos1 = (Lottos) o;
        return Objects.equals(lottos, lottos1.lottos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lottos);
    }

    public void forEach(Consumer<Lotto> action) {
        lottos.forEach(action);
    }

    public Integer count() {
        return lottos.size();
    }

    public Integer winningCount(Lotto winningLotto, Integer bonusNumber, Rank rank) {
        return Math.toIntExact(lottos.stream()
                .filter(lotto -> lotto.rank(winningLotto, bonusNumber) == rank)
                .count());
    }

    public BigDecimal totalProfitRate(Lotto winningLotto, Integer bonusNumber) {
        return winningAmount(winningLotto, bonusNumber).divide(purchaseAmount(), 2, RoundingMode.FLOOR);
    }

    public Lottos add(Lottos another) {
        List<Lotto> result = new ArrayList<>();
        result.addAll(lottos);
        result.addAll(another.lottos);
        return new Lottos(result);
    }

    private BigDecimal winningAmount(Lotto winningLotto, Integer bonusNumber) {
        return Arrays.stream(Rank.values())
                .reduce(BigDecimal.ZERO,
                        (amount, rank) -> amount.add(rank.winningAmountByCount(winningCount(winningLotto, bonusNumber, rank))),
                        BigDecimal::add);
    }

    private BigDecimal purchaseAmount() {
        return BigDecimal.valueOf(count()).multiply(LottoStore.LOTTO_PRICE);
    }
}
