class Test3 {
int a;
int b;
int c;
int d;
int e;
int addIt(int x, int y, int z){
    return(x+y+z);
}

int main(){

    a=6;
    b=10;
	d=8;
    if((a+b-d)<10){
	 e = addIt(a, b, d);
	 print e;
	 if(5<6){
	   print "in";
	   }
	   print "out";
	}
return;
}