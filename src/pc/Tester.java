package pc;

public class Tester {
	public static void main(String[] args) {
		ClientWindow cw = new ClientWindow();
		//
			cw.print("test");
			cw.print("testest");
			cw.print("testestest");
			cw.print("testestestest");
		while(true) {
			cw.print(cw.awaitNextInput());
		}
	}
}
