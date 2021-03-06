package com.fundamentos.springboot.fundamentos;

import com.fundamentos.springboot.fundamentos.bean.MyBean;
import com.fundamentos.springboot.fundamentos.bean.MyBeanWithDependency;
import com.fundamentos.springboot.fundamentos.bean.MyBeanWithProperties;
import com.fundamentos.springboot.fundamentos.component.ComponentDependency;
import com.fundamentos.springboot.fundamentos.entity.User;
import com.fundamentos.springboot.fundamentos.pojo.UserPojo;
import com.fundamentos.springboot.fundamentos.repository.UserRepository;
import com.fundamentos.springboot.fundamentos.service.UserService;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class FundamentosApplication implements CommandLineRunner {

	private final Log LOGGER = LogFactory.getLog(FundamentosApplication.class);

	private ComponentDependency componentDependency;
	private MyBean mybean;
	private MyBeanWithDependency myBeanWithDependency;
	private MyBeanWithProperties myBeanWithProperties;
	private UserPojo userPojo;
	private UserRepository userRepository;
	private UserService userService;

	public FundamentosApplication(@Qualifier("componentTwoImplement") ComponentDependency componentDependency, MyBean mybean, MyBeanWithDependency myBeanWithDependency, MyBeanWithProperties myBeanWithProperties, UserPojo userPojo, UserRepository userRepository, UserService userService){
		this.componentDependency = componentDependency;
		this.mybean = mybean;
		this.myBeanWithDependency = myBeanWithDependency;
		this.myBeanWithProperties = myBeanWithProperties;
		this.userPojo = userPojo;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	public static void main(String[] args) {
		SpringApplication.run(FundamentosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        //ejemplosAnteriores();
		saveUsersInDatabase();
		getInformationJpqlFromUser();
		saveWithErrorTransaction();
	}

	private void saveWithErrorTransaction(){
		User test1 = new User("Test1Transactional", "Test1Transactional@domain.com", LocalDate.now());
		User test2 = new User("Test2Transactional", "Test2Transactional@domain.com", LocalDate.now());
		User test3 = new User("Test3Transactional", "Test1Transactional@domain.com", LocalDate.now());
		User test4 = new User("Test4Transactional", "Test4Transactional@domain.com", LocalDate.now());

		List<User> users = Arrays.asList(test1, test2, test3, test4);
		try {
			userService.saveTransactional(users);
		}catch (Exception e){
			LOGGER.error("esta es una excepcion dentro del metodo transactional "+e );
		}


		userService.getAllUsers().stream().forEach(user -> LOGGER.info("este es el usuario dentro del metodo transactional "+user));
	}

	private void getInformationJpqlFromUser(){

		LOGGER.info("Usuario con el metodo findByUserEmail"+ userRepository.findByUserEmail("edgardo@domain.com").orElseThrow(()->new RuntimeException("No se encontr?? el usuario")));
		userRepository.findAndSort("user", Sort.by("id").descending()).stream().forEach(user -> LOGGER.info("Usuario con el metodo sort" +user));
	userRepository.findByName("John").stream().forEach(user -> LOGGER.info("usuario con query method"+user.toString()));
	LOGGER.info("usuario con query method findByEmailAndName"+ userRepository.findByEmailAndName("daniela@domain.com", "Daniela").orElseThrow(() -> new RuntimeException("usuario no encontrado")));
	userRepository.findByNameLike("%user%").stream().forEach(user-> LOGGER.info("usuario findByNameLike"+user));
	userRepository.findByNameOrEmail(null, "john@domain.com").stream().forEach(user-> LOGGER.info("usuario findByNameLike"+user));
	userRepository.findByBirthDateBetween(LocalDate.of(2021, 03, 1), LocalDate.of(2022,01,2)).stream().forEach(user-> LOGGER.info("usuario con intervalo de fechas"+user));
	userRepository.findByNameContainingOrderByIdDesc("user").stream().forEach(user -> LOGGER.info("usuario encontrado con like y ordenado"+user));
	LOGGER.info("el usuario a partir del named parameter es: "+userRepository.getAllByBirthDateAndEmail(LocalDate.of(2021, 03, 20 ), "john@domain.com").orElseThrow(()-> new RuntimeException("No se encontro el usuario a partir del named parameter")));
	}

	private void saveUsersInDatabase(){
		User user1 = new User("John", "john@domain.com", LocalDate.of(2021, 03, 20 ));
		User user2 = new User("John", "edgardo@domain.com", LocalDate.of(2021, 04, 25 ));
		User user3 = new User("Daniela", "daniela@domain.com", LocalDate.of(2021, 05, 23 ));
		User user4 = new User("user1", "user1@domain.com", LocalDate.of(2021, 04, 17 ));
		User user5 = new User("user2", "andrea@domain.com", LocalDate.of(2022, 01, 02 ));
		User user6 = new User("user3", "maria@domain.com", LocalDate.of(2021, 06, 15 ));
		User user7 = new User("user4", "diana@domain.com", LocalDate.of(2021, 07, 10 ));
		User user8 = new User("Pedro", "pedro@domain.com", LocalDate.of(2021, 12, 07 ));
		User user9 = new User("Paulo", "paulo@domain.com", LocalDate.of(2021, 10, 03 ));
		User user10 = new User("Fulvia", "fulvia@domain.com", LocalDate.of(2021, 10, 05 ));
		List<User> list = Arrays.asList(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10);
		list.stream().forEach(userRepository::save);
	}

    private void ejemplosAnteriores(){
        componentDependency.saludar();
        mybean.print();
        myBeanWithDependency.printWithDependency();
        System.out.println(myBeanWithProperties.function());
        System.out.println(userPojo.getEmail() + "-"+userPojo.getPassword());

        try {
            //error
            int value = 10/0;
            LOGGER.info("Mi valor: "+ value);
        } catch(Exception e) {
            LOGGER.error("Esto es un error al dividir por cero "+e.getStackTrace());
        }
    }
}
