package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Ventana de inicio de sesión.
 * Valida el usuario contra la BD usando UsuarioDAO.
 * Si las credenciales son correctas abre Principal, si no muestra error.
 */
public class Login extends JFrame {

    private JTextField     txtUsername = new JTextField(15);
    private JPasswordField txtPassword = new JPasswordField(15);
    private JButton        btnEntrar   = new JButton("Entrar");
    private JLabel         lblError    = new JLabel(" ");   // espacio para no "saltar" el layout

    public Login() {
        super("Cine Granada — Login");
        initUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);  // centra en pantalla
        setVisible(true);
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título del formulario
        JLabel lblTitulo = new JLabel("Cine Granada", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Campo usuario
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        // Campo contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        // Etiqueta de error (visible solo si falla el login)
        lblError.setForeground(Color.RED);
        lblError.setFont(new Font("Arial", Font.ITALIC, 11));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblError, gbc);

        // Botón principal de entrada
        // Si te piden cambiar las acciones o estilo del botón de login, aquí se define su fondo, color y fuente
        btnEntrar.setBackground(new Color(0xB71C1C));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridy = 4;
        panel.add(btnEntrar, gbc);

        // Enlace para ir al registro
        // Nota: Es un JLabel que actúa como enlace usando código HTML y un MouseListener
        JLabel lblRegistro = new JLabel("<html><a href='#'>¿No tienes cuenta? Regístrate</a></html>",
                SwingConstants.CENTER);
        lblRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegistro.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose(); // Cierra la ventana actual
                new Registro(); // Abre la ventana de registro
            }
        });
        gbc.gridy = 5;
        panel.add(lblRegistro, gbc);

        // Acciones del botón y tecla Enter en contraseña
        // Al pulsar el botón o dar Enter en el campo de texto de contraseña, se llama al método intentarLogin()
        btnEntrar.addActionListener(e -> intentarLogin());
        txtPassword.addActionListener(e -> intentarLogin());

        setContentPane(panel);
    }

    /** Comprueba las credenciales contra la BD y abre Principal si son válidas. */
    private void intentarLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Rellena todos los campos.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAOImpl();
            Usuario u = dao.validar(user, pass);
            if (u != null) {
                dispose();
                new Principal(u);   // abrimos la ventana principal con el usuario validado
            } else {
                lblError.setText("Usuario o contraseña incorrectos.");
                txtPassword.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
