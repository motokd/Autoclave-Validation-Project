package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController implements Initializable {
	@FXML
	private TableView<Autoclaves> tv_autoclaves;
	@FXML
	private TableColumn<Autoclaves, Integer> col_entryNumber;
	@FXML
	private TableColumn<Autoclaves, String> col_propertyID;
	@FXML
	private TableColumn<Autoclaves, Date> col_runDate;
	@FXML
	private TableColumn<Autoclaves, Integer> colRunTime;
	@FXML
	private TableColumn<Autoclaves, Integer> col_temperature;
	@FXML
	private TableColumn<Autoclaves, Integer> col_pressure;
	@FXML
	private TableColumn<Autoclaves, String> col_tResult;
	@FXML
	private TableColumn<Autoclaves, String> col_loadDesc;
	@FXML
	private TextField tf_entryNo;
	@FXML
	private DatePicker df_startTime = new DatePicker();
	@FXML
	private TextField tf_runtime;
	@FXML
	private TextField tf_propertyID;
	@FXML
	private TextField tf_tResult;
	@FXML
	private TextField tf_pressure;
	@FXML
	private TextField tf_temperature;
	@FXML
	private TextArea tf_loadDesc;
	@FXML
	private Button updateBtn;
	@FXML
	private Button insertBtn;
	
	
	@FXML
	private void handleButtonAction(ActionEvent event) {
		if (event.getSource() == updateBtn) {
			updateRecord();
		} if (event.getSource() == insertBtn) {
			insertRecord();
			
			// this is how you get the table to refresh.... commented out as this is "next weeks" work along with data validation
			//tv_autoclaves.refresh();
			//showAutoclaves();
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		updateBtn.setDisable(true);
		addListenerForTable();
		showAutoclaves();
	}
	
	// create connection to the database
	public Connection getConnection() {
		Connection conn;
		try {
			// everything up to localhost:3306/ should be correct. Only thing that may need corrected is the table name......
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "xxx", "xxx");
			System.out.println("connection successful");
			return conn;
		}catch(Exception ex){
			System.out.println("Error: " + ex.getMessage());
			return null;
		}
	}
	
	public ObservableList<Autoclaves> getAutoclavesList(){
		ObservableList<Autoclaves> autoclaveList = FXCollections.observableArrayList();
		Connection conn = getConnection();
		String query = "SELECT * FROM atest";
		Statement st;
		ResultSet rs;
		
		try {
			System.out.println("Attempting Connection...");
			st = conn.createStatement();
			rs = st.executeQuery(query);
			Autoclaves autoclaves;
			while(rs.next()) {
				System.out.println("reading info...");
				autoclaves = new Autoclaves(rs.getInt("entry_no"), rs.getString("prop_id"), rs.getDate("run_date"), rs.getInt("runtime"), rs.getInt("temperature"), rs.getInt("pressure"), rs.getString("t_result"), rs.getString("load_desc"));
				autoclaveList.add(autoclaves);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return autoclaveList;
	}
	
	/**
	 * This is the READ operation
	 */
	public void showAutoclaves() {
		ObservableList<Autoclaves> list = getAutoclavesList();
		
		// need to finish this!!!!!!!!!!!
		col_entryNumber.setCellValueFactory(new PropertyValueFactory<Autoclaves, Integer>("entry_no"));
		col_propertyID.setCellValueFactory(new PropertyValueFactory<Autoclaves, String>("prop_id"));
		col_runDate.setCellValueFactory(new PropertyValueFactory<Autoclaves, Date>("run_date"));
		colRunTime.setCellValueFactory(new PropertyValueFactory<Autoclaves, Integer>("runtime"));
		col_temperature.setCellValueFactory(new PropertyValueFactory<Autoclaves, Integer>("temperature"));
		col_pressure.setCellValueFactory(new PropertyValueFactory<Autoclaves, Integer>("pressure"));
		col_loadDesc.setCellValueFactory(new PropertyValueFactory<Autoclaves, String>("load_desc"));
		col_tResult.setCellValueFactory(new PropertyValueFactory<Autoclaves, String>("t_result"));
		
		tv_autoclaves.setItems(list);
		
	}
	
	/**
	 * This is the UPDATE operation
	 */
	private void updateRecord() {
		String query = "UPDATE atest SET prop_id = ?, run_date = ? , runtime = ?, temperature = ?, pressure = ?, t_result = ?, load_desc = ? WHERE entry_no = ?";
		Connection conn = getConnection();
		try {
			PreparedStatement pst = conn.prepareStatement(query);
			
			// reformat the date to the acceptable MySQL format which is 'yyyy-mm-dd'
			String fDate = df_startTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			
			if (tf_propertyID.getText().equals("") || tf_propertyID.getText().trim().isEmpty()
					|| tf_tResult.getText().equals("") || tf_tResult.getText().trim().isEmpty()
					|| tf_temperature.getText().equals("") || tf_temperature.getText().trim().isEmpty()
					|| tf_pressure.getText().equals("") || tf_pressure.getText().trim().isEmpty()
					|| tf_runtime.getText().equals("") || tf_runtime.getText().trim().isEmpty()) {
				System.out.println("Error: Missing fields");
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("UPDATE UNSUCCESSFUL");
				alert.setContentText("Update Unsuccessful: Missing data, please make sure all fields are completed");
				alert.showAndWait();
			} else {
				// set the values
				pst.setString(1, tf_propertyID.getText());
				pst.setString(2, fDate);
				pst.setString(3, tf_runtime.getText());
				pst.setString(4, tf_temperature.getText());
				pst.setString(5, tf_pressure.getText());
				pst.setString(6, tf_tResult.getText());
				pst.setString(7, tf_loadDesc.getText());
				pst.setString(8, tf_entryNo.getText());
				pst.executeUpdate();
				insertBtn.setDisable(false);
				
				//clear the fields
				clearFields();
				
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		showAutoclaves();
	}
	
	private void insertRecord() {
		String query = "INSERT INTO sys.atest (entry_no, prop_id, run_date, runtime, temperature, pressure, t_result, load_desc) values (?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = getConnection();
		try {
			PreparedStatement pst = conn.prepareStatement(query);
			// reformat the date to the acceptable MySQL format which is 'yyyy-mm-dd'
			String fDate = df_startTime.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			
			int entryInt = Integer.parseInt(tf_entryNo.getText());
			int runInt = Integer.parseInt(tf_runtime.getText());
			int tempInt = Integer.parseInt(tf_temperature.getText());
			int pressInt = Integer.parseInt(tf_pressure.getText());
			
			pst.setInt(1, entryInt);
			pst.setString(2, tf_propertyID.getText());
			pst.setString(3, fDate);
			pst.setInt(4, runInt);
			pst.setInt(5, tempInt);
			pst.setInt(6, pressInt);
			pst.setString(7, tf_tResult.getText());
			pst.setString(8, tf_loadDesc.getText());
			pst.executeUpdate();
			
			//clear the fields
			clearFields();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * This method is the event handler for when a user clicks on a row in the table. The listener returns the values at each column
	 * into the applicable field.
	 */
	private void addListenerForTable(){
		tv_autoclaves.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if(newSelection != null) {
				updateBtn.setDisable(false);
				insertBtn.setDisable(true);
				tf_propertyID.setText(newSelection.getProp_id());
				tf_loadDesc.setText(newSelection.getLoad_desc());
				tf_tResult.setText(newSelection.getT_result());
				tf_runtime.setText(Integer.toString(newSelection.getRuntime()));
				tf_temperature.setText(Integer.toString(newSelection.getTemperature()));
				tf_pressure.setText(Integer.toString(newSelection.getPressure()));
				tf_entryNo.setText(Integer.toString(newSelection.getEntry_no()));
				
				// reformat the date to localdate and add it to the datepicker field
				Date date = newSelection.getRun_date();
				LocalDate localDate = LocalDate.parse( new SimpleDateFormat("yyyy-MM-dd").format(date) );
				df_startTime.setValue(localDate);
				
				tf_entryNo.setEditable(false); // set to false so that you cannot accidently edit the wrong entry and/or an entry that does not exist
			} else {
				updateBtn.setDisable(true);
			}
		});
	}
	
    private void executeQuery(String query) {
        Connection conn = getConnection();
        Statement st;
        try{
            st = conn.createStatement();
            st.executeUpdate(query);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * This method clears the fields after an UPDATE or INSERT operation is performed.
     */
    private void clearFields() {
    	tf_propertyID.setText(null);
    	tf_runtime.setText(null);
    	tf_temperature.setText(null);
    	tf_pressure.setText(null);
    	tf_tResult.setText(null);
    	tf_loadDesc.setText(null);
    	tf_entryNo.setText(null);
    	df_startTime.setValue(null);
    }

}
