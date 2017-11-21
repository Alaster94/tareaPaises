package sample;

import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Controller {
    @FXML
    private JFXListView<String> lbPaises;

    @FXML
    private TextField txtPais;

    @FXML
    private JFXListView<String> lbCiudades;

    @FXML
    private JFXListView<String> lbDetalle;

    @FXML
    private Label lblError;

    @FXML
    private Label lbTitulo;

    private String paisSeleccionado;

    private String ciudadSeleccionada;

    private ObservableList<String> paises = FXCollections.observableArrayList();
    private ObservableList<String> listaCiudades = FXCollections.observableArrayList();
    private ObservableList<String> listaDetalles = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        lbPaises.setItems(paises);
        lbCiudades.setItems(listaCiudades);
        lbDetalle.setItems(listaDetalles);
        lbPaises.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                System.out.println("Elemento viejo: " + oldValue + ", elemento nuevo: " + newValue);
                paisSeleccionado = newValue;
                listaCiudades.clear();
                try {
                    Connection con = Main.getConexion();
                    Statement stnt = con.createStatement();
                    String sql = "SELECT Name FROM city WHERE countryCode = (SELECT Code FROM country WHERE Name='"+paisSeleccionado+"')";
                    ResultSet resultado = stnt.executeQuery(sql);
                    while (resultado.next()){
                        listaCiudades.add(resultado.getString("Name"));
                    }
                }catch (SQLException e){
                    System.out.println(e.getMessage());

                }

            }
        });
        lbCiudades.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ciudadSeleccionada = newValue;
                listaDetalles.clear();
                try {
                    Connection con = Main.getConexion();
                    Statement stn = con.createStatement();
                    String sql = "SELECT District, Population FROM city WHERE Name = '"+ciudadSeleccionada+"'";
                    ResultSet resultado = stn.executeQuery(sql);
                    while (resultado.next()){
                        listaDetalles.add(resultado.getString("District"));
                        listaDetalles.add(resultado.getString("Population"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void buscarPais(KeyEvent keyEvent) {
//        lbTitulo.setText(String.valueOf(keyEvent.getCode()));
//        if (keyEvent.getCode() == KeyCode.ENTER){
//            paises.add(txtPais.getText());
//            txtPais.clear();
//        }
        String nombreBusqueda = txtPais.getText().trim();
        paises.clear();
        if (nombreBusqueda.length()>=1){//Realiza la busqueda solo si se ha escrito mas de 2 caracteres
            Connection con = Main.getConexion();

            try {
                Statement stnt = con.createStatement();
                String sql = "SELECT Name FROM country WHERE Name LIKE '"+ nombreBusqueda+ "%'";
                ResultSet resultado = stnt.executeQuery(sql);
                while (resultado.next()){
                    paises.add(resultado.getString(1));
                }
                resultado.close();
            } catch (SQLException e) {
                lblError.setText(e.getMessage());
//                e.printStackTrace();
            }
        }

    }


}
