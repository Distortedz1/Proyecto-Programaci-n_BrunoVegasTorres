package view;

import dao.ClienteDAOImpl;
import dao.EmpleadoDAOImpl;
import model.Cliente;
import model.Empleado;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Registro extends JFrame {

    private JTextField     txtUsername  = new JTextField(15);
    private JPasswordField txtPassword  = new JPasswordField(15);
    private JTextField     txtEmail     = new JTextField(15);
    private JTextField     txtNombre    = new JTextField(15);
    private JTextField     txtApellidos = new JTextField(15);
    private JTextField     txtDNI       = new JTextField(15);
    private JComboBox<String> cmbRol    = new JComboBox<>(new String[]{"cliente", "empleado"});

    // Solo teléfono para cliente, empleado no tiene campos extra
    private JTextField txtTelefono = new JTextField(15);

    private JPanel panelForm;
    private GridBagConstraints gbc;

    public Registro() {
        super("Registro — Cine Granada");
        initUI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitulo = new JLabel("Crear cuenta nueva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panelForm.add(lblTitulo, gbc);

        int fila = 1;
        fila = addCampo("Usuario:",    txtUsername,  fila);
        fila = addCampo("Contraseña:", txtPassword,  fila);
        fila = addCampo("Email:",      txtEmail,     fila);
        fila = addCampo("Nombre:",     txtNombre,    fila);
        fila = addCampo("Apellidos:",  txtApellidos, fila);
        fila = addCampo("DNI:",        txtDNI,       fila);
        fila = addCampo("Rol:",        cmbRol,       fila);

        final int filaBase = fila;
        actualizarCamposDinamicos(filaBase);

        cmbRol.addActionListener(e -> {
            Component[] comps = panelForm.getComponents();
            for (Component c : comps) {
                GridBagConstraints g = ((GridBagLayout) panelForm.getLayout()).getConstraints(c);
                if (g.gridy >= filaBase) panelForm.remove(c);
            }
            actualizarCamposDinamicos(filaBase);
            panelForm.revalidate();
            panelForm.repaint();
            pack();
        });

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnVolver    = new JButton("Volver al Login");
        btnRegistrar.setBackground(new Color(0xB71C1C));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.addActionListener(e -> registrar());
        btnVolver.addActionListener(e -> { dispose(); new Login(); });
        panelBtns.add(btnRegistrar);
        panelBtns.add(btnVolver);

        setLayout(new BorderLayout());
        add(panelForm, BorderLayout.CENTER);
        add(panelBtns, BorderLayout.SOUTH);
    }

    private int addCampo(String etiqueta, JComponent campo, int fila) {
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = fila;
        panelForm.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        panelForm.add(campo, gbc);
        return fila + 1;
    }

    private void actualizarCamposDinamicos(int filaInicio) {
        String rol = (String) cmbRol.getSelectedItem();
        if ("cliente".equals(rol)) {
            addCampo("Teléfono:", txtTelefono, filaInicio);
        }
        // empleado no necesita campos extra
    }

    private void registrar() {
        String user      = txtUsername.getText().trim();
        String pass      = new String(txtPassword.getPassword());
        String email     = txtEmail.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni       = txtDNI.getText().trim();
        String rol       = (String) cmbRol.getSelectedItem();

        if (user.isEmpty() || pass.isEmpty() || email.isEmpty() ||
            nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Rellena todos los campos obligatorios.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if ("cliente".equals(rol)) {
                String telefono = txtTelefono.getText().trim();
                if (telefono.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El teléfono es obligatorio para clientes.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Cliente c = new Cliente(user, pass, email, nombre, apellidos, dni,
                        telefono, 0);
                new ClienteDAOImpl().registrar(c);
            } else {
                // Empleado: generar número automático y puesto por defecto
                String numEmpleado = "EMP-" + System.currentTimeMillis();
                String puesto = "taquillero";
                Empleado emp = new Empleado(user, pass, email, nombre, apellidos, dni, numEmpleado, puesto);
                new EmpleadoDAOImpl().registrar(emp);
            }

            JOptionPane.showMessageDialog(this, "¡Cuenta creada correctamente!",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}