package Controller; 
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel; 
import javax.swing.JPasswordField;
import javax.swing.JTextField; 
import model.RMOS; 
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent; 

/**
 * @(#)AdminLogin.java  
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 13/3/2014
 * @Description: This class will show the admin login interface of RMOS
 */

public class AdminLogin  
{ 
	String user = null;
	String password = null;
	String status = null;
	private RMOS rmos;
	public AdminLogin(RMOS rmos) {
		 this.rmos = rmos;
	}

    	/* login in interface */		    
	public void login()
	{
		final JFrame start = new JFrame();

		start.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		start.setTitle("Welcome to EcoRecycle system");
		start.setSize(640,480);  
		
	    	JLabel sl3=new JLabel("Admin login"); 
	    	JButton sb1 = new JButton("Log In"); 
	        JLabel sl1=new JLabel("Username: ");
	        JLabel sl2=new JLabel("Password: ");
	        final JTextField st1=new JTextField(null,15); 
	        final JPasswordField sp1=new JPasswordField(null,15);   
	        final Container scon=start.getContentPane();
	
	    	scon.setLayout(null);
	    	sl3.setBounds(390,170,230,30); 	// setBounds(x, y, width, height);
	    	sl1.setBounds(390,220,70,30);
	    	st1.setBounds(460,220,120,30);
	    	sl2.setBounds(390,270,70,30);
	    	sp1.setBounds(460,270,120,30);
	    	sb1.setBounds(470,330,70,30);
		
	    	scon.add(sl3);
	    	scon.add(sl1);
	    	scon.add(sl2);
	    	scon.add(st1);
	    	scon.add(sp1);
	    	scon.add(sb1);
	    
	    	start.setVisible(true);
	
	    	/* 	the "login in" button, when press this, system will get user id and password,
	    	        and if password is correct, it will access the main interface 	*/
	    	
	    	sb1.addActionListener (
	    		new ActionListener() {
				public void actionPerformed(ActionEvent event) {
		    			String username=st1.getText(); 
					String pass=sp1.getText(); 
	    			
		    			try { 
		    		    		if (username.equals("admin") && pass.equals("admin")) {     		
			    		    		//go to main interface
			    		    		st1.setText("");
			    		    		sp1.setText("");
			    		    		scon.removeAll();
			    	        		start.setVisible(false);
			    	        		new MainInterface(rmos);        
		    		    		} else {
		    		    			JOptionPane.showMessageDialog(null ,"The username or password you entered is incorrect.") ;	 
		    		    		 }  
	    			   	} 
					catch (Exception e){ 
						JOptionPane.showMessageDialog(null ,"The username or password you entered is incorrect.") ;
						e.printStackTrace(); 
					} 
	    			 }
		    	}
		);
	
	    	 /* 	the background of login in interface  */
	    	 ((JComponent) start.getContentPane()).setOpaque(false);  
	         java.net.URL url = AdminLogin.class.getResource("/gui/images/star.jpg");
	         ImageIcon img = new ImageIcon(url);
		 JLabel background = new JLabel(img);
		 start.getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
		 background.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
	}
}

