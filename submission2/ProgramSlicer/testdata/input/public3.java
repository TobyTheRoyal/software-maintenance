// (14,{x}), (16,{z})
class Public3 {                     
    public static void main() {
        int x = 0;
        int y = 3;
	int z = x + y;

	if ( x == y)
	{
		z = 0;
	}
	else
	{
		x = 11;
	}
	z = x + y;    
    }
}
