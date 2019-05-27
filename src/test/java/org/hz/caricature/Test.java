package org.hz.caricature;

import java.io.IOException;

import org.hz.caricature.view.AcgDbView;
import org.hz.caricature.view.AcgDownFormDbView;

public class Test {
	
	public void downFormDB(){
		AcgDownFormDbView v = new AcgDownFormDbView();
		v.startTask();
	}
	
	public void insertDB(){
		AcgDbView v = new AcgDbView();
//		v.startTask("http://m.mwspyxgs.com/shaonv/2016/0907/3540.html");
		v.startTask();
	}
	
	public static void main(String[] args) throws IOException {
		Test t = new Test();
		t.insertDB();
		
	}

}
