//(21,{d})
class Custom4 {
    public static void main() {
        int b = 1;
        int a = b * 2;
        int d = 1;
        int c = a + d;
        while (a < c + 5)
        {
            a = a + b;
            d = c - a;
        }
        if (a > c)
        {
            b = a + 2;
        }
        else
        {
            c = c - 10;
        }
        d = a + c;
    }
}
 
