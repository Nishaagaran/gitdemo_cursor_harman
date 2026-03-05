public class Factorial {

    public static long factorialIterative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static long factorialRecursive(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorialRecursive(n - 1);
    }

    public static void main(String[] args) {
        int n = 5; // change this value to test other numbers

        System.out.println("Iterative factorial of " + n + " = " + factorialIterative(n));
        System.out.println("Recursive factorial of " + n + " = " + factorialRecursive(n));
    }
}
