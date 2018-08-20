package in.co.cg.spring.mmbank.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import in.co.cg.spring.mmbank.pojo.CurrentAccount;
import in.co.cg.spring.mmbank.pojo.Customer;
import in.co.cg.spring.mmbank.pojo.SavingsAccount;
import in.co.cg.spring.mmbank.service.BankAccountService;
import in.co.cg.spring.mmbank.service.BankAccountServiceI;

@Controller
public class BankController {
		
	
	
	@RequestMapping("/")
	public String home() {   //Redirects to home page
		
		return "home";
	}
	@RequestMapping("/index") // redirects to home page when user click on home button
	public String index() {
		
		return "home";
	}
	@RequestMapping("/WithdrawForm") // redirects to withdraw form when user click on withdraw button
	public String withdraw() {
		
		return "WithdrawForm";
	}
	
	@RequestMapping("/AddNewAccount") // redirects to Add new account form when user click on Add account button
	public String addNewAccount() {
		
		return "AddNewAccount";
	}
	
	
	@RequestMapping("/DepositForm") // redirects to deposit form when user click on deposit button
	public String deposit() {
		
		return "DepositForm";
	}
	
	@RequestMapping("/FundTransfer") // redirects to fundtranfer  form when user click on fundtranfer button
	public String fundTranfer() {
	
		return "FundTransfer";
	}
	
	@RequestMapping("/searchAccount") // redirects to Search  form when user click on Search button
	public String searchCustomer() {
	
		return "searchAccount";
	}
	
	@Autowired
	BankAccountServiceI accountService;

	@RequestMapping("/addNew")
	public String addAcount(
		
			@RequestParam("c_Name") String c_Name ,
			@RequestParam("c_Email") String c_Email,
			@RequestParam("c_contact") long c_contact,
			@RequestParam("c_dob") String c_dob,
			@RequestParam("gender") String gender,
			@RequestParam("c_Address") String c_Address,
				@RequestParam("c_AccType") String c_AccType,
				@RequestParam("c_amount") String c_amount,
			@RequestParam("c_salary") String c_salary,
			@RequestParam("c_ODLimit") String c_ODLimit,
			Model model	)
	 {
			DateTimeFormatter JAVAFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Date format pattern for date of birth i.e. yyyy/MM/dd
		LocalDate dateOfBirth = LocalDate.parse(c_dob, JAVAFormat); // initializing customer date of birth to dateOfBirth
		Customer customer = new Customer(c_Name, c_Email,c_contact ,dateOfBirth ,c_Address ,gender );// invoking constructor of Customer  Class
	System.out.println("HELLOW");
																														
		if(c_AccType.equals("sav")) { // checks if account is Saving Account

			if ("c_salary".equals("salary")) { // checks if Saving Account is salaried

				SavingsAccount savingsAccount = new SavingsAccount(customer, true, "Savings"); // invoking constructor  of SavingsAccount Class

				accountService.AddNewAccount(savingsAccount); // Invoking AddNewAccount

				model.addAttribute("accNO", accountService.getAccountNo() - 1); // Invoking getAccountNo
				return "createSuccess";
			}

			else {// checks if Saving Account is Not salaried

				SavingsAccount savingsAccount = new SavingsAccount(customer,Double.parseDouble(c_amount), "Savings", false);// invoking constructor of SavingsAccount Class

				accountService.AddNewAccount(savingsAccount); // Invoking AddNewAccount

				model.addAttribute("accNO", accountService.getAccountNo() - 1); // Invoking getAccountNo
				return "createSuccess";
			}
		}

		else {

			CurrentAccount currentAccount = new CurrentAccount(customer,Double.parseDouble(c_amount) , "Current", Double.parseDouble(c_ODLimit)); // invoking constructor of CurrentAccount Class

			accountService.AddNewAccount(currentAccount); // Invoking AddNewAccount

			model.addAttribute("accNO", accountService.getAccountNo() - 1);
			return "createSuccess";
		}
		
	
	}

	@RequestMapping("/viewaccount")
	public String viewAllAccount(Model model) {
		model.addAttribute("viewaccount", accountService.viewAccount());
		return "viewaccount";
	}
	
	@RequestMapping("/deposit")
	public String depositAmount(@RequestParam("acNO")int acNO, @RequestParam("amt") double c_amount ,Model model) {
		model.addAttribute("viewcust", accountService.deposit(c_amount, acNO));
		return "depositSuccess";
	}
	
	@RequestMapping("/withdraw")
	public String withdrawAmount(@RequestParam("acNo")int accNo, @RequestParam("amt")double amount ,Model model) {
		double amountT=amount;
		TreeMap<Integer, Integer> deno= new TreeMap<Integer, Integer>(); //map to store notecount and value
		int notes[]= {2000,500,200,100,50,20,10,5,2,1};//all available notes
		int totalNotes=0,eachNoteCount=0;
		
		for(int i=0;i<10;i++) {
			eachNoteCount=(int) (amount/notes[i]);  //count no. of notes
			if(eachNoteCount!=0) {
				deno.put(notes[i], eachNoteCount); //put note value and count in map
			}
			totalNotes+=eachNoteCount; //total note count
			amount%=notes[i]; //remaining amount
			
		}
		model.addAttribute("toatlNotes",totalNotes); //Setting total number of notes withdrawSuccess.jsp
		model.addAttribute("deno",deno); //Setting deno attribute for denomination map to withdrawSuccess.jsp
		model.addAttribute("amount",amountT); //Setting deno attribute for denomination map to withdrawSuccess.jsp
		model.addAttribute("viewcust", accountService.withdrawl(amountT, accNo)); //Invoking deposit
		return "withdrawSuccess";
	}
	
	@RequestMapping("/viewcust")
	public String viewAllCustomer(Model model) {
		model.addAttribute("viewcust", accountService.viewCustomer());
		return "ViewAllCustomers";
	}

	@RequestMapping("/fundtransfer")
	public String fundT(@RequestParam("sender")int sender,@RequestParam("reciever")int reciever,@RequestParam("remarks")String remarks,@RequestParam("amt")double amount,Model model) {
		model.addAttribute("transfer", accountService.Fundtransfer(sender, reciever, amount, remarks)); //Invoking Fundtransfer
		model.addAttribute("reciever", reciever);
		model.addAttribute("sender", sender);
		model.addAttribute("remarks", remarks);
		model.addAttribute("amount", amount);
		return "transferSuccess";
	}
	
	@RequestMapping("/searchCust")
	public String searchCust(@RequestParam("acS") int accno,Model model) {

		model.addAttribute("searchCust", accountService.searchAccount(accno)); //Invoking searchAccount
		return "searchResult";
	}
	
	@RequestMapping("/updateInfo")
	public String updateInfo(@RequestParam("accId") int accno,Model model) {
		model.addAttribute("updateCust", accountService.searchAccount(accno)); //Invoking searchAccount
		return "UpdateCustomerForm";
	}
	
	@RequestMapping("/updateSuccess")
	public String updateSuccess(@RequestParam("accId") int accno,@RequestParam("c_contact") long contactNumber,@RequestParam("c_dob") String c_dob,@RequestParam("c_Name") String customerName,@RequestParam("c_Email") String emailId,Model model) {
		DateTimeFormatter	JAVAFormat1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dateOfBirth = LocalDate.parse(c_dob, JAVAFormat1);
		accountService.updateInfo(accno, contactNumber, dateOfBirth, customerName, emailId); //Invoking updateInfo
		model.addAttribute("updatesuccess", accountService.searchAccount(accno));//Invoking searchAccount
		return "UpdateSucess";
	}
}
