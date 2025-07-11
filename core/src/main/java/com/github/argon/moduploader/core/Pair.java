package com.github.argon.moduploader.core;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class Pair<L, R> implements Comparable<Pair<L, R>> {
    private final L left;
    private final R right;

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(@NonNull Pair<L, R> otherPair) {
        if (left instanceof Comparable<?>) {
            return ((Comparable<L>) left).compareTo(otherPair.left);
        }
        if (right instanceof Comparable<?>) {
            return ((Comparable<R>) right).compareTo(otherPair.right);
        }

        return 0;
    }
}
