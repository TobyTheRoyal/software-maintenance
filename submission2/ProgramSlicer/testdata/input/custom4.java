//(13,{d})
class Custom4 {
    public static void main() {
        int r = a;
        int b=(a+2);
        int c=(a-2);
        int d=(a+c);
        while(c < a)
        {
            c=(c+1);
            while(b < d)
            {
                d=(c-a);
            }
            if(c == a)
            {
                b=(a+c);
            }
            a=(2*b);
        }
        int res=(a+b+c+d);
        int unused = res;
    }
}