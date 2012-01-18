import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import java.util.Collection;
import java.util.Iterator;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import edu.umd.cs.findbugs.annotations.NonNull;

public class Functions {

  private Functions() {
  }


  /**
   * Flattens a list of lists into a single list.
   * Like flatMap in Scala or bind in Haskell, but restricted to Iterables only thanks to Java's laughable type system.
   */
  public static <A, B> Iterable<B> flatTransform(Iterable<A> i, Function<? super A, ? extends Iterable<B>> f) {
    return concat(transform(i, f));
  }

  /**
   * Sums Integers.
   * @param  ints a collection of {@code Integer} objects
   * @return sum of ints
   * @throws NullPointerException if {@code ints} or any of its elements is null
   */
  public static int sum(@NonNull Iterable<Integer> ints) {
    int sum = 0;
    for (int anInt : ints) {
      sum += anInt;
    }
    return sum;
  }


  /**
   * Returns true if any element of the given list is true.
   *
   * @param bools A list to check for any element being true.
   * @return true if any element of the given list is true. False otherwise.
   */
  public static boolean or(@NonNull Iterable<Boolean> bools) {
    boolean sum = false;
    for (Boolean anInt : bools) {
      sum |= anInt;
    }
    return sum;
  }


  /**
   * Groups elements into a list of lists, with grouping determined by the given function.
   *
   * @param i the elements to group
   * @param f determines the property to group by
   * @return A list of grouped elements.
   */
  public static <A, B> Collection<? extends Collection<? extends A>> group(Iterable<? extends A> i,
      Function<A, B> f) {
    return Multimaps.index(i, f).asMap().values();
  }


  /**
   * Returns true if all {@code B}s are equal
   */
  public static <A, B> boolean elementsEqual(Iterable<A> iterable, Function<A, B> function) {
    return elementsEqual(transform(iterable, function));
  }

  /**
   * Returns true if all elements in the List are equal
   */
  public static boolean elementsEqual(Iterable<?> iterable) {
    return Sets.newHashSet(iterable).size() < 2;
  }


  interface Function2<A, B, C> {

    C apply(A a, B b);
  }

  /**
   * Left Fold.
   * (a, b, c, d), initial -> f(f(f(f(initial,a), b), c), d)
   */
  public static <X, Y> X fold(final Iterable<? extends Y> gen, final X initial, final
  Function2<? super X, ? super Y, ? extends X> function) {
    final Iterator<? extends Y> it = gen.iterator();
    if (!it.hasNext()) {
      return initial;
    }
    X acc = initial;
    while (it.hasNext()) {
      acc = function.apply(acc, it.next());
    }
    return acc;
  }

  /**
   * Sums Integers.
   * @param  ints a collection of {@code Integer} objects
   * @return sum of ints
   * @throws NullPointerException if {@code ints} or any of its elements is null
   */
  public static int sumAlternative(@NonNull Iterable<Integer> ints) {
    return fold(ints, 0, ADD);
  }

  public static Optional<Integer> sum(@NonNull Iterable<Optional<Integer>> ints) {
    return fold(somes(ints), Optional.<Integer>absent(), ADD_OPTIONAL);
  }

  /**
   * Returns all the values in the given list.
   *
   * @param as The list of potential values to get actual values from.
   * @return All the values in the given list.
   */
  public static <A> Iterable<A> somes(final Iterable<Optional<A>> as) {
    return transform(filter(as, IS_PRESENT), new Function<Optional<A>, A>() {
      public A apply(Optional<A> input) {
        return input.get();
      }
    });
  }

  private static final Function2<Integer, Integer, Integer> ADD = new Function2<Integer, Integer, Integer>() {
    @Override
    public Integer apply(Integer a, Integer b) {
      return a + b;
    }
  };

  private static final Function2<Optional<Integer>, Integer, Optional<Integer>> ADD_OPTIONAL = new Function2<Optional<Integer>, Integer, Optional<Integer>>() {
    @Override
    public Optional<Integer> apply(Optional<Integer> a, Integer b) {
      return a.isPresent() ? Optional.of(a.get() + b) : Optional.of(b);
    }
  };

  private static final Predicate<Optional<?>> IS_PRESENT = new Predicate<Optional<?>>() {
    public boolean apply(Optional<?> input) {
      return input.isPresent();
    }
  };

  public static <A> Function<Optional<A>, Boolean> isSome_() {
    return new Function<Optional<A>, Boolean>() {
      public Boolean apply(final Optional<A> a) {
        return a.isPresent();
      }
    };
  }


}
