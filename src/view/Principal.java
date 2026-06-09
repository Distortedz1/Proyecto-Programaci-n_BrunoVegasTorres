package view;

import dao.*;
import dto.EntradaDTO;
import model.*;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Ventana principal de la aplicación.
 * Se adapta según el rol del usuario:
 *   - empleado → Películas / Entradas / Clientes
 *   - cliente  → Cartelera / Mis Entradas
 */
public class Principal extends JFrame {

    // ── DAOs ───────────────────────────────────────────────────────────────────
    private PeliculaDAO peliculaDAO = new PeliculaDAOImpl();
    private ClienteDAO  clienteDAO  = new ClienteDAOImpl();
    private EntradaDAO  entradaDAO  = new EntradaDAOImpl();
    private UsuarioDAO  usuarioDAO  = new UsuarioDAOImpl();
    private SesionDAO   sesionDAO   = new SesionDAOImpl();

    // Usuario que ha iniciado sesión
    private Usuario usuarioActual;

    // Componentes del centro
    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private JPanel            panelOps;   // panel derecho con botones/formularios
    private JLabel            lblModulo;  // título del módulo activo

    // Colores corporativos reutilizados en toda la vista
    private static final Color ROJO  = new Color(0xB71C1C);
    private static final Color VERDE = new Color(0x2E7D32);
    private static final Color AZUL  = new Color(0x1565C0);

    // ── Constructor ────────────────────────────────────────────────────────────
    public Principal(Usuario usuario) {
        super("Cine Granada");
        this.usuarioActual = usuario;
        initUI();

        // El módulo inicial depende del rol
        if (usuarioActual.getRol().equals("empleado")) {
            cargarModulo("Peliculas");
        } else {
            cargarModulo("Cartelera");
        }

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setVisible(true);
    }

    // ── Construcción de la UI ──────────────────────────────────────────────────
    private void initUI() {
        setLayout(new BorderLayout());

        // Barra roja superior con nombre del cine y usuario
        JPanel barTop = new JPanel(new BorderLayout());
        barTop.setBackground(ROJO);
        barTop.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        JLabel lblTitulo = new JLabel("Cine Granada");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 17));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblUser = new JLabel("Hola, " + usuarioActual.getNombre() + " (" + usuarioActual.getRol() + ")");
        lblUser.setForeground(Color.WHITE);
        
        JLabel lblClima = new JLabel("Cargando clima...");
        lblClima.setForeground(Color.WHITE);
        lblClima.setFont(new Font("Arial", Font.PLAIN, 13));
        lblClima.setHorizontalAlignment(SwingConstants.CENTER);
        
        barTop.add(lblTitulo, BorderLayout.WEST);
        barTop.add(lblClima,  BorderLayout.CENTER);
        barTop.add(lblUser,   BorderLayout.EAST);
        
        cargarClima(lblClima);

        // Menú de sesión (cerrar sesión)
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemSalir = new JMenuItem("Cerrar sesión");
        itemSalir.addActionListener(e -> cerrarSesion());
        menuSesion.add(itemSalir);
        menuBar.add(menuSesion);
        setJMenuBar(menuBar);

        // Sidebar lateral izquierda — botones de navegación según el rol
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ROJO);
        sidebar.setPreferredSize(new Dimension(160, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // =========================================================================================
        // ¿CÓMO AÑADIR UN BOTÓN NUEVO EN EL MENÚ LATERAL IZQUIERDO?
        // 1. Añade aquí una línea: sidebar.add(btnNav("Texto Botón", "NombreModulo"));
        //    (Usa el bloque IF si es solo para 'empleado', o el ELSE si es solo para 'cliente')
        // 2. Deja separación vertical con: sidebar.add(Box.createVerticalStrut(5));
        // 3. Ve al switch del método 'cargarModulo' (más abajo) y pon un nuevo 'case' con tu lógica.
        // =========================================================================================
        if (usuarioActual.getRol().equals("empleado")) {
            // Botones para empleados
            sidebar.add(btnNav("Peliculas",    "Peliculas"));
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnNav("Entradas",     "Entradas"));
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnNav("Clientes",     "Clientes"));
        } else {
            // Botones para clientes
            sidebar.add(btnNav("Cartelera",    "Cartelera"));
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnNav("Mis Entradas", "MisEntradas"));
        }

        // Panel central con tabla y panel de operaciones
        JPanel centro = new JPanel(new BorderLayout(8, 8));
        centro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centro.setBackground(new Color(0xF5F7FA));

        lblModulo = new JLabel("");
        lblModulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblModulo.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));

        // Tabla no editable
        modeloTabla = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);

        // Panel derecho para formularios y acciones del módulo activo
        panelOps = new JPanel();
        panelOps.setBackground(Color.WHITE);
        panelOps.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        panelOps.setPreferredSize(new Dimension(230, 0));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, panelOps);
        split.setResizeWeight(0.75);
        split.setBorder(null);

        centro.add(lblModulo, BorderLayout.NORTH);
        centro.add(split,     BorderLayout.CENTER);

        add(barTop,  BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(centro,  BorderLayout.CENTER);
    }

    /**
     * Crea un botón de navegación para la sidebar.
     * Al pulsarlo carga el módulo correspondiente.
     * 
     * SI TE PIDEN CAMBIAR EL DISEÑO DEL BOTÓN LATERAL:
     * - setBackground(ROJO) -> Color de fondo.
     * - setForeground(Color.WHITE) -> Color del texto.
     * - setMaximumSize(new Dimension(ancho, alto)) -> Tamaño.
     * - setFont(...) -> Tipo de letra.
     */
    private JButton btnNav(String texto, String modulo) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setBackground(ROJO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Este listener escucha el clic y llama a cargarModulo pasándole el identificador
        btn.addActionListener(e -> cargarModulo(modulo));
        return btn;
    }

    /**
     * Botón decorativo / informativo — no realiza ninguna acción.
     * Se usa para mostrar la versión de la app en la sidebar.
     */
    private JButton btnInfo(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(160, 35));
        btn.setBackground(new Color(0x8B0000));  // rojo más oscuro para diferenciarlo
        btn.setForeground(new Color(0xCCCCCC));  // texto gris claro
        btn.setFont(new Font("Arial", Font.ITALIC, 11));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setEnabled(false);                   // desactivado = no interactivo
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        return btn;
    }

    /** 
     * Decide qué método cargar según el nombre del módulo.
     * 
     * SI AÑADES UN NUEVO BOTÓN EN LA SIDEBAR:
     * Debes crear un método para mostrar el contenido de tu nuevo módulo
     * (por ejemplo, mostrarNuevoModulo()) y añadir un 'case' en este switch:
     *      case "NombreModulo": mostrarNuevoModulo(); break;
     */
    private void cargarModulo(String modulo) {
        lblModulo.setText(modulo);
        tabla.setSelectionModel(new DefaultListSelectionModel());
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        switch (modulo) {
            case "Peliculas":    mostrarPeliculas();   break;
            case "Entradas":     mostrarEntradas();    break;
            case "Clientes":     mostrarClientes();    break;
            case "Cartelera":    mostrarCartelera();   break;
            case "MisEntradas":  mostrarMisEntradas(); break;
        }
    }

    // ── EMPLEADO: PELÍCULAS ────────────────────────────────────────────────────

    private void mostrarPeliculas() {
        try {
            // Cargamos todas las películas desde la BD
            List<Pelicula> lista = peliculaDAO.listarTodos();
            modeloTabla.setColumnIdentifiers(new String[]{"ID", "Título", "Género", "Duración", "Director", "Año", "Precio"});
            modeloTabla.setRowCount(0);
            for (Pelicula p : lista) {
                modeloTabla.addRow(new Object[]{
                    p.getId(), p.getTitulo(), p.getGenero(), p.getDuracion(),
                    p.getDirector(), p.getAnio(), String.format("%.2f€", p.getPrecio())
                });
            }

            // Formulario del panel derecho
            panelOps.removeAll();
            panelOps.setLayout(new BoxLayout(panelOps, BoxLayout.Y_AXIS));
            panelOps.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            JTextField fTitulo   = new JTextField(12);
            JTextField fGenero   = new JTextField(12);
            JTextField fDuracion = new JTextField(12);
            JTextField fDirector = new JTextField(12);
            JTextField fAnio     = new JTextField(12);
            JTextField fPrecio   = new JTextField(12);

            panelOps.add(new JLabel("Título:"));     panelOps.add(fTitulo);
            panelOps.add(Box.createVerticalStrut(4));
            panelOps.add(new JLabel("Género:"));     panelOps.add(fGenero);
            panelOps.add(Box.createVerticalStrut(4));
            panelOps.add(new JLabel("Duración:"));   panelOps.add(fDuracion);
            panelOps.add(Box.createVerticalStrut(4));
            panelOps.add(new JLabel("Director:"));   panelOps.add(fDirector);
            panelOps.add(Box.createVerticalStrut(4));
            panelOps.add(new JLabel("Año:"));        panelOps.add(fAnio);
            panelOps.add(Box.createVerticalStrut(4));
            panelOps.add(new JLabel("Precio (€):")); panelOps.add(fPrecio);
            panelOps.add(Box.createVerticalStrut(10));

            // Al seleccionar una fila, rellenamos el formulario automáticamente
            tabla.getSelectionModel().addListSelectionListener(ev -> {
                if (!ev.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    int r = tabla.getSelectedRow();
                    fTitulo.setText(modeloTabla.getValueAt(r, 1).toString());
                    fGenero.setText(modeloTabla.getValueAt(r, 2).toString());
                    fDuracion.setText(modeloTabla.getValueAt(r, 3).toString());
                    fDirector.setText(modeloTabla.getValueAt(r, 4).toString());
                    fAnio.setText(modeloTabla.getValueAt(r, 5).toString());
                    fPrecio.setText(modeloTabla.getValueAt(r, 6).toString().replace("€", ""));
                }
            });

            // =========================================================================================
            // BOTONES DE OPERACIÓN (CRUD) EN EL PANEL DERECHO:
            // btnOp(String texto, Color color) crea botones estilizados.
            // Si te piden añadir un nuevo botón para hacer otra acción sobre las películas, debes:
            // 1. Crear el botón: JButton btnMiAccion = btnOp("Mi Acción", Color.BLUE);
            // 2. Darle funcionalidad: btnMiAccion.addActionListener(e -> {... tu código aquí ...});
            // 3. Meterlo en el panel: panelOps.add(btnMiAccion);
            // =========================================================================================
            JButton btnNuevo   = btnOp("Nuevo",    new Color(0x455A64));
            JButton btnGuardar = btnOp("Guardar",  VERDE);
            JButton btnElim    = btnOp("Eliminar", ROJO);

            // Nuevo: limpia el formulario para insertar
            btnNuevo.addActionListener(e -> {
                tabla.clearSelection();
                fTitulo.setText(""); fGenero.setText(""); fDuracion.setText("");
                fDirector.setText(""); fAnio.setText(""); fPrecio.setText("");
            });

            // Guardar: inserta si no hay fila seleccionada, actualiza si la hay
            btnGuardar.addActionListener(e -> {
                try {
                    int fila = tabla.getSelectedRow();
                    if (fila >= 0) {
                        // Actualizar película existente
                        int id = (int) modeloTabla.getValueAt(fila, 0);
                        peliculaDAO.actualizar(new Pelicula(id,
                            fTitulo.getText().trim(), fGenero.getText().trim(),
                            Integer.parseInt(fDuracion.getText().trim()),
                            fDirector.getText().trim(),
                            Integer.parseInt(fAnio.getText().trim()),
                            Double.parseDouble(fPrecio.getText().trim().replace(",", "."))));
                    } else {
                        // Insertar película nueva
                        peliculaDAO.insertar(new Pelicula(
                            fTitulo.getText().trim(), fGenero.getText().trim(),
                            Integer.parseInt(fDuracion.getText().trim()),
                            fDirector.getText().trim(),
                            Integer.parseInt(fAnio.getText().trim()),
                            Double.parseDouble(fPrecio.getText().trim().replace(",", "."))));
                    }
                    JOptionPane.showMessageDialog(this, "Película guardada.");
                    mostrarPeliculas();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            btnElim.addActionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una película."); return; }
                int id = (int) modeloTabla.getValueAt(fila, 0);
                if (JOptionPane.showConfirmDialog(this, "¿Eliminar película?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                try { peliculaDAO.eliminar(id); mostrarPeliculas(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });

            panelOps.add(btnNuevo);
            panelOps.add(Box.createVerticalStrut(5));
            panelOps.add(btnGuardar);
            panelOps.add(Box.createVerticalStrut(5));
            panelOps.add(btnElim);
            panelOps.revalidate();
            panelOps.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── EMPLEADO: ENTRADAS ─────────────────────────────────────────────────────

    private void mostrarEntradas() {
        try {
            // Usamos EntradaDTO para mostrar nombre del cliente y título de película (JOIN)
            List<EntradaDTO> lista = entradaDAO.listarTodos();
            modeloTabla.setColumnIdentifiers(new String[]{"ID", "Cliente", "Película", "Cantidad", "Precio/u", "Subtotal", "Fecha", "Sala"});
            modeloTabla.setRowCount(0);
            for (EntradaDTO e : lista) {
                modeloTabla.addRow(new Object[]{
                    e.getId(), e.getClienteNombre(), e.getTituloPelicula(),
                    e.getCantidad(), String.format("%.2f€", e.getPrecioUnidad()),
                    String.format("%.2f€", e.getSubtotal()), e.getFecha(), e.getSala()
                });
            }

            panelOps.removeAll();
            panelOps.setLayout(new BoxLayout(panelOps, BoxLayout.Y_AXIS));
            panelOps.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            panelOps.add(new JLabel("Selecciona una entrada"));
            panelOps.add(Box.createVerticalStrut(10));

            JButton btnElim = btnOp("Eliminar Entrada", ROJO);
            btnElim.addActionListener(e -> {
                int fila = tabla.getSelectedRow();
                if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una entrada."); return; }
                int id = (int) modeloTabla.getValueAt(fila, 0);
                if (JOptionPane.showConfirmDialog(this, "¿Eliminar entrada #" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                try { entradaDAO.eliminar(id); mostrarEntradas(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            });

            panelOps.add(btnElim);
            panelOps.revalidate();
            panelOps.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── EMPLEADO: CLIENTES ─────────────────────────────────────────────────────

    private void mostrarClientes() {
        try {
            List<Cliente> lista = clienteDAO.listarTodos();
            modeloTabla.setColumnIdentifiers(new String[]{"ID", "Username", "Nombre", "Apellidos", "Email", "DNI", "Teléfono"});
            modeloTabla.setRowCount(0);
            for (Cliente c : lista) {
                modeloTabla.addRow(new Object[]{
                    c.getId(), c.getUsername(), c.getNombre(),
                    c.getApellidos(), c.getEmail(), c.getDni(), c.getTelefono()
                });
            }

            panelOps.removeAll();
            panelOps.setLayout(new BoxLayout(panelOps, BoxLayout.Y_AXIS));
            panelOps.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            panelOps.add(new JLabel("Lista de clientes registrados"));
            panelOps.revalidate();
            panelOps.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── CLIENTE: CARTELERA + COMPRAR ──────────────────────────────────────────

    private void mostrarCartelera() {
        try {
            List<Pelicula> lista = peliculaDAO.listarTodos();
            modeloTabla.setColumnIdentifiers(new String[]{"Título", "Género", "Duración (min)", "Director", "Año", "Precio"});
            modeloTabla.setRowCount(0);
            for (Pelicula p : lista) {
                modeloTabla.addRow(new Object[]{
                    p.getTitulo(), p.getGenero(), p.getDuracion(),
                    p.getDirector(), p.getAnio(), String.format("%.2f€", p.getPrecio())
                });
            }

            panelOps.removeAll();
            panelOps.setLayout(new BoxLayout(panelOps, BoxLayout.Y_AXIS));
            panelOps.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            // ComboBox con todas las películas disponibles
            JComboBox<Pelicula> cmbPelicula = new JComboBox<>();
            for (Pelicula p : lista) cmbPelicula.addItem(p);

            // ComboBox de DÍAS
            JComboBox<String> cmbDia = new JComboBox<>();
            
            // ComboBox de HORARIOS para el día seleccionado
            JComboBox<Sesion> cmbHorario = new JComboBox<>();
            cmbHorario.setRenderer(new javax.swing.DefaultListCellRenderer() {
                public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                    if (value instanceof Sesion) {
                        Sesion s = (Sesion) value;
                        value = s.getHora() + " — " + s.getSala();
                    }
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
            });
            JLabel lblSinSesiones = new JLabel("Sin sesiones");
            lblSinSesiones.setForeground(Color.GRAY);
            lblSinSesiones.setVisible(false);

            JTextField txtCantidad = new JTextField("1");

            // Cargar películas → días → horarios
            Runnable cargarDias = () -> {
                cmbDia.removeAllItems();
                cmbHorario.removeAllItems();
                Pelicula sel = (Pelicula) cmbPelicula.getSelectedItem();
                if (sel == null) return;
                try {
                    List<Sesion> sesiones = sesionDAO.listarPorPelicula(sel.getId());
                    java.util.Set<String> dias = new java.util.LinkedHashSet<>();
                    java.util.Map<String, java.util.List<Sesion>> sesionesPorDia = new java.util.LinkedHashMap<>();
                    
                    for (Sesion s : sesiones) {
                        String fecha = s.getFecha();
                        String diaNombre = obtenerNombreDia(fecha);
                        String diaCompleto = diaNombre + " (" + fecha + ")";
                        dias.add(diaCompleto);
                        sesionesPorDia.computeIfAbsent(diaCompleto, k -> new java.util.ArrayList<>()).add(s);
                    }
                    
                    if (dias.isEmpty()) {
                        lblSinSesiones.setVisible(true);
                        cmbDia.setVisible(false);
                        cmbHorario.setVisible(false);
                    } else {
                        lblSinSesiones.setVisible(false);
                        cmbDia.setVisible(true);
                        cmbHorario.setVisible(true);
                        
                        for (String dia : dias) cmbDia.addItem(dia);
                        
                        // Al seleccionar día, cargar horarios
                        for (java.awt.event.ActionListener al : cmbDia.getActionListeners()) {
                            cmbDia.removeActionListener(al);
                        }
                        cmbDia.addActionListener(e -> {
                            cmbHorario.removeAllItems();
                            String diaSeleccionado = (String) cmbDia.getSelectedItem();
                            if (diaSeleccionado != null && sesionesPorDia.containsKey(diaSeleccionado)) {
                                for (Sesion s : sesionesPorDia.get(diaSeleccionado)) {
                                    cmbHorario.addItem(s);
                                }
                            }
                        });
                        
                        if (cmbDia.getItemCount() > 0) {
                            cmbDia.setSelectedIndex(0);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar sesiones: " + ex.getMessage());
                }
            };

            cmbPelicula.addActionListener(e -> cargarDias.run());

            panelOps.add(new JLabel("Pelicula:"));
            panelOps.add(cmbPelicula);
            panelOps.add(Box.createVerticalStrut(8));
            panelOps.add(new JLabel("Dia:"));
            panelOps.add(cmbDia);
            panelOps.add(Box.createVerticalStrut(8));
            panelOps.add(new JLabel("Horario (sala):"));
            panelOps.add(cmbHorario);
            panelOps.add(lblSinSesiones);
            panelOps.add(Box.createVerticalStrut(8));
            panelOps.add(new JLabel("Cantidad:"));
            panelOps.add(txtCantidad);
            panelOps.add(Box.createVerticalStrut(14));

            // Sincronizar selección en la tabla → combo de películas
            tabla.getSelectionModel().addListSelectionListener(ev -> {
                if (!ev.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                    String titSel = modeloTabla.getValueAt(tabla.getSelectedRow(), 0).toString();
                    for (int i = 0; i < cmbPelicula.getItemCount(); i++) {
                        if (cmbPelicula.getItemAt(i).getTitulo().equals(titSel)) {
                            cmbPelicula.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            });

            JButton btnComprar = btnOp("Comprar Entrada", VERDE);
            btnComprar.addActionListener(e -> {
                try {
                    Sesion sesion = (Sesion) cmbHorario.getSelectedItem();
                    if (sesion == null) {
                        JOptionPane.showMessageDialog(this, "Selecciona un horario disponible.");
                        return;
                    }
                    int cant = Integer.parseInt(txtCantidad.getText().trim());

                    Cliente cliente = clienteDAO.buscarPorId(usuarioActual.getId());
                    if (cliente == null) {
                        JOptionPane.showMessageDialog(this, "Tu usuario no tiene perfil de cliente.");
                        return;
                    }
                    Entrada entrada = new Entrada(cliente.getId(), sesion.getId(), cant);
                    entradaDAO.insertar(entrada);
                    JOptionPane.showMessageDialog(this,
                        "Entrada comprada correctamente.\n" +
                        sesion.getFecha() + " a las " + sesion.getHora());
                    cargarModulo("MisEntradas");
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser un número.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            });

            panelOps.add(btnComprar);
            panelOps.revalidate();
            panelOps.repaint();

            // Cargamos los días de la primera película al abrir
            cargarDias.run();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── Métodos auxiliares para Cartelera ──────────────────────────────────────

    private String obtenerNombreDia(String fecha) {
        try {
            java.time.LocalDate ld = java.time.LocalDate.parse(fecha);
            java.time.DayOfWeek dow = ld.getDayOfWeek();
            String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
            return dias[dow.getValue() - 1];
        } catch (Exception e) {
            return fecha;
        }
    }

    // ── CLIENTE: MIS ENTRADAS ──────────────────────────────────────────────────

    private void mostrarMisEntradas() {
        try {
            // Filtramos todas las entradas para mostrar solo las del usuario actual
            List<EntradaDTO> todas = entradaDAO.listarTodos();
            String nombreCompleto = usuarioActual.getNombre() + " " + usuarioActual.getApellidos();

            modeloTabla.setColumnIdentifiers(new String[]{"ID", "Película", "Cantidad", "Precio/u", "Subtotal", "Fecha y Hora", "Sala"});
            modeloTabla.setRowCount(0);
            for (EntradaDTO e : todas) {
                if (e.getClienteNombre().equals(nombreCompleto)) {
                    modeloTabla.addRow(new Object[]{
                        e.getId(), e.getTituloPelicula(), e.getCantidad(),
                        String.format("%.2f€", e.getPrecioUnidad()),
                        String.format("%.2f€", e.getSubtotal()),
                        e.getFecha(), e.getSala()
                    });
                }
            }

            panelOps.removeAll();
            panelOps.setLayout(new BoxLayout(panelOps, BoxLayout.Y_AXIS));
            panelOps.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            panelOps.add(new JLabel("Tus entradas compradas"));
            panelOps.revalidate();
            panelOps.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── CLIMA ──────────────────────────────────────────────────────────────────

    /**
     * Abre la ventana de clima.
     * Pasa la lista de películas para que ClimaCartelera pueda sugerir una.
     */
    // ── SESIÓN ─────────────────────────────────────────────────────────────────

    private void cerrarSesion() {
        int conf = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            dispose();
            new Login();
        }
    }

    // ── HELPERS ────────────────────────────────────────────────────────────────

    /** Crea un botón estilizado para el panel de operaciones (panel derecho). */
    private JButton btnOp(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return btn;
    }

    private void cargarClima(JLabel lblClima) {
        new Thread(() -> {
            try {
                String API_CLIMA = "https://api.open-meteo.com/v1/forecast?latitude=37.18&longitude=-3.60&current=temperature_2m,weathercode&timezone=Europe/Madrid";
                java.net.HttpURLConnection con = (java.net.HttpURLConnection) java.net.URI.create(API_CLIMA).toURL().openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(8000);
                con.setReadTimeout(10000);

                StringBuilder sb = new StringBuilder();
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(con.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {
                    String linea;
                    while ((linea = br.readLine()) != null) sb.append(linea);
                }

                String json = sb.toString();
                int currentIdx = json.indexOf("\"current\":");
                if (currentIdx < 0) currentIdx = 0;

                // Temp
                String tempBuscar = "\"temperature_2m\":";
                int tempIdx = json.indexOf(tempBuscar, currentIdx);
                int tempFin = tempIdx + tempBuscar.length();
                while (tempFin < json.length() && json.charAt(tempFin) != ',' && json.charAt(tempFin) != '}' && json.charAt(tempFin) != '"') tempFin++;
                double temp = Double.parseDouble(json.substring(tempIdx + tempBuscar.length(), tempFin).replace("\"", "").trim());

                // Weathercode
                String wmoBuscar = "\"weathercode\":";
                int wmoIdx = json.indexOf(wmoBuscar, currentIdx);
                int wmoFin = wmoIdx + wmoBuscar.length();
                while (wmoFin < json.length() && json.charAt(wmoFin) != ',' && json.charAt(wmoFin) != '}' && json.charAt(wmoFin) != '"') wmoFin++;
                int wmo = Integer.parseInt(json.substring(wmoIdx + wmoBuscar.length(), wmoFin).replace("\"", "").trim());

                String condicion;
                if (wmo == 0)         condicion = "Despejado";
                else if (wmo <= 3)    condicion = "Parcialmente nublado";
                else if (wmo <= 49)   condicion = "Niebla";
                else if (wmo <= 67)   condicion = "Lluvia";
                else if (wmo <= 77)   condicion = "Nieve";
                else if (wmo <= 82)   condicion = "Chubascos";
                else if (wmo >= 95)   condicion = "Tormenta";
                else                  condicion = "Nublado";

                String textoClima = String.format("Granada: %.1f °C, %s", temp, condicion);
                SwingUtilities.invokeLater(() -> lblClima.setText(textoClima));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> lblClima.setText("Granada: Clima no disponible"));
            }
        }).start();
    }
}
