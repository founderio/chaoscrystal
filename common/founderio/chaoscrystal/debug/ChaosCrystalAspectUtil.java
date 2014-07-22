package founderio.chaoscrystal.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import founderio.chaoscrystal.ChaosCrystalMain;
import founderio.chaoscrystal.aspects.Aspect;
import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;
import founderio.chaoscrystal.aspects.Node;
import founderio.chaoscrystal.aspects.NodePointType;
import founderio.util.ItemUtil;

public class ChaosCrystalAspectUtil {

	private final ChaosRegistry registry;
	private AspectModule module = new AspectModule();
	private Node selectedNode = null;
	
	
	private JFrame frame;
	private JList list;
	private JTextField txtModuleName;
	private JFileChooser fileChooser;
	private JTextField textField_L;
	private JTextField textField_G;
	private JSpinner spinner_L;
	private JSpinner spinner_G;
	private JRadioButton rdbtnItem_L;
	private JRadioButton rdbtnBlock_L;
	private JRadioButton rdbtnItem_G;
	private JRadioButton rdbtnBlock_G;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	
	private JLabel[] aspectLabels = new JLabel[Aspect.values().length];
	private JSpinner[] aspectSpinners = new JSpinner[Aspect.values().length];
	/**
	 * Launch the application.
	 */
	public static void open(final ChaosRegistry registry) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ChaosCrystalAspectUtil window = new ChaosCrystalAspectUtil(registry);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChaosCrystalAspectUtil(ChaosRegistry registry) {
		this.registry = registry;
		initialize();
		loadDefs();
	}

	public void loadModule(AspectModule module) {
		this.module = module;
		setSelectedNode(null);
		txtModuleName.setText(module.getName());
		loadDefs();
	}
	//TODO: File paths, handle saving & loading directly in here.

	public void saveModule(AspectModule module) {
		module.setName(txtModuleName.getText());
	}
	
	public void setSelectedNode(Node node) {
		if(selectedNode != null) {
			selectedNode.getLesser().setType(rdbtnBlock_L.isSelected() ? NodePointType.BLOCK : NodePointType.ITEM);
			selectedNode.getLesser().setUniqueName(textField_L.getText());
			selectedNode.getLesser().setMeta((Integer)spinner_L.getValue());
	
			selectedNode.getGreater().setType(rdbtnBlock_G.isSelected() ? NodePointType.BLOCK : NodePointType.ITEM);
			selectedNode.getGreater().setUniqueName(textField_G.getText());
			selectedNode.getGreater().setMeta((Integer)spinner_G.getValue());
			
			int[] aspects = new int[Aspect.values().length];
			
			for(int a = 0; a < Aspect.values().length; a++) {
				aspects[a] = (Integer)aspectSpinners[a].getValue();
			}
			
			selectedNode.setAspects(aspects);
		}
		selectedNode = node;
		if(selectedNode != null) {
			textField_L.setText(node.getLesser().getUniqueName());
			spinner_L.setValue(node.getLesser().getMeta());
			rdbtnBlock_L.setSelected(node.getLesser().getType() == NodePointType.BLOCK);
			
			textField_G.setText(node.getGreater().getUniqueName());
			spinner_G.setValue(node.getGreater().getMeta());
			rdbtnBlock_G.setSelected(node.getGreater().getType() == NodePointType.BLOCK);
			
			int[] aspects = selectedNode.getAspects();
			
			for(int a = 0; a < Aspect.values().length; a++) {
				aspectSpinners[a].setValue(aspects[a]);
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 696, 429);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(5);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		list = new JList();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				setSelectedNode(module.getNodes().get(list.getSelectedIndex()));
			}
		});
		splitPane.setLeftComponent(list);
		list.setPreferredSize(new Dimension(10, 0));

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.NORTH);

		JLabel lblModuleName = new JLabel("Module Name");
		panel_1.add(lblModuleName);

		txtModuleName = new JTextField();
		txtModuleName.setText("Module Name");
		panel_1.add(txtModuleName);
		txtModuleName.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel lblLesser = new JLabel("Lesser");
		lblLesser.setEnabled(false);
		GridBagConstraints gbc_lblLesser = new GridBagConstraints();
		gbc_lblLesser.insets = new Insets(0, 0, 5, 5);
		gbc_lblLesser.gridx = 0;
		gbc_lblLesser.gridy = 0;
		panel_2.add(lblLesser, gbc_lblLesser);
		
		JLabel lblType_L = new JLabel("Type");
		GridBagConstraints gbc_lblType_L = new GridBagConstraints();
		gbc_lblType_L.anchor = GridBagConstraints.EAST;
		gbc_lblType_L.insets = new Insets(0, 0, 5, 5);
		gbc_lblType_L.gridx = 0;
		gbc_lblType_L.gridy = 1;
		panel_2.add(lblType_L, gbc_lblType_L);
		
		rdbtnItem_L = new JRadioButton("ITEM");
		rdbtnItem_L.setSelected(true);
		buttonGroup.add(rdbtnItem_L);
		GridBagConstraints gbc_rdbtnItem_L = new GridBagConstraints();
		gbc_rdbtnItem_L.anchor = GridBagConstraints.WEST;
		gbc_rdbtnItem_L.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnItem_L.gridx = 1;
		gbc_rdbtnItem_L.gridy = 1;
		panel_2.add(rdbtnItem_L, gbc_rdbtnItem_L);
		
		rdbtnBlock_L = new JRadioButton("BLOCK");
		buttonGroup.add(rdbtnBlock_L);
		GridBagConstraints gbc_rdbtnBlock_L = new GridBagConstraints();
		gbc_rdbtnBlock_L.anchor = GridBagConstraints.WEST;
		gbc_rdbtnBlock_L.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBlock_L.gridx = 2;
		gbc_rdbtnBlock_L.gridy = 1; 
		panel_2.add(rdbtnBlock_L, gbc_rdbtnBlock_L);
		
		JButton btnSetToLifeless_L = new JButton("Set to Lifeless");
		btnSetToLifeless_L.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnBlock_L.isSelected()) {
					textField_L.setText(GameData.getBlockRegistry().getNameForObject(ChaosCrystalMain.blockLifeless));
				} else {
					textField_L.setText(GameData.getItemRegistry().getNameForObject(ChaosCrystalMain.itemLifelessShard));
				}
				spinner_L.setValue(0);
			}
		});
		GridBagConstraints gbc_btnSetToLifeless_L = new GridBagConstraints();
		gbc_btnSetToLifeless_L.anchor = GridBagConstraints.EAST;
		gbc_btnSetToLifeless_L.gridwidth = 4;
		gbc_btnSetToLifeless_L.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetToLifeless_L.gridx = 3;
		gbc_btnSetToLifeless_L.gridy = 1;
		panel_2.add(btnSetToLifeless_L, gbc_btnSetToLifeless_L);
		
		JLabel lblUniqueName_L = new JLabel("Unique Name");
		GridBagConstraints gbc_lblUniqueName_L = new GridBagConstraints();
		gbc_lblUniqueName_L.anchor = GridBagConstraints.EAST;
		gbc_lblUniqueName_L.insets = new Insets(0, 0, 5, 5);
		gbc_lblUniqueName_L.gridx = 0;
		gbc_lblUniqueName_L.gridy = 2;
		panel_2.add(lblUniqueName_L, gbc_lblUniqueName_L);
		
		textField_L = new JTextField();
		GridBagConstraints gbc_textField_L = new GridBagConstraints();
		gbc_textField_L.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_L.gridwidth = 2;
		gbc_textField_L.insets = new Insets(0, 0, 5, 5);
		gbc_textField_L.gridx = 1;
		gbc_textField_L.gridy = 2;
		panel_2.add(textField_L, gbc_textField_L);
		textField_L.setColumns(10);
		
		JLabel lblMeta_L = new JLabel("Meta");
		GridBagConstraints gbc_lblMeta_L = new GridBagConstraints();
		gbc_lblMeta_L.insets = new Insets(0, 0, 5, 5);
		gbc_lblMeta_L.gridx = 3;
		gbc_lblMeta_L.gridy = 2;
		panel_2.add(lblMeta_L, gbc_lblMeta_L);
		
		spinner_L = new JSpinner();
		spinner_L.setModel(new SpinnerNumberModel(Integer.valueOf(ItemUtil.WILDCARD_META), Integer.valueOf(0), null, Integer.valueOf(1)));
		spinner_L.setPreferredSize(new Dimension(80, 28));
		GridBagConstraints gbc_spinner_L = new GridBagConstraints();
		gbc_spinner_L.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_L.insets = new Insets(0, 0, 5, 5);
		gbc_spinner_L.gridx = 4;
		gbc_spinner_L.gridy = 2;
		panel_2.add(spinner_L, gbc_spinner_L);
		
		JButton btnWildcard_L = new JButton("*");
		btnWildcard_L.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinner_L.setValue(ItemUtil.WILDCARD_META);
			}
		});
		btnWildcard_L.setPreferredSize(new Dimension(29, 29));
		GridBagConstraints gbc_btnWildcard_L = new GridBagConstraints();
		gbc_btnWildcard_L.insets = new Insets(0, 0, 5, 5);
		gbc_btnWildcard_L.gridx = 5;
		gbc_btnWildcard_L.gridy = 2;
		panel_2.add(btnWildcard_L, gbc_btnWildcard_L);
		
		JButton btnBrowse_L = new JButton("...");
		btnBrowse_L.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = findBlockOrItemName(rdbtnBlock_L.isSelected() ? NodePointType.BLOCK : NodePointType.ITEM);
				if(value != null) {
					textField_L.setText(value);
				}
			}
		});
		btnBrowse_L.setPreferredSize(new Dimension(29, 29));
		GridBagConstraints gbc_btnBrowse_L = new GridBagConstraints();
		gbc_btnBrowse_L.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse_L.gridx = 6;
		gbc_btnBrowse_L.gridy = 2;
		panel_2.add(btnBrowse_L, gbc_btnBrowse_L);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 7;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		panel_2.add(separator, gbc_separator);
		
		JLabel lblGreater = new JLabel("Greater");
		lblGreater.setEnabled(false);
		GridBagConstraints gbc_lblGreater = new GridBagConstraints();
		gbc_lblGreater.insets = new Insets(0, 0, 5, 5);
		gbc_lblGreater.gridx = 0;
		gbc_lblGreater.gridy = 4;
		panel_2.add(lblGreater, gbc_lblGreater);
		
		
		JLabel lblType_G = new JLabel("Type");
		GridBagConstraints gbc_GblType_G = new GridBagConstraints();
		gbc_GblType_G.anchor = GridBagConstraints.EAST;
		gbc_GblType_G.insets = new Insets(0, 0, 5, 5);
		gbc_GblType_G.gridx = 0;
		gbc_GblType_G.gridy = 5;
		panel_2.add(lblType_G, gbc_GblType_G);
		
		rdbtnItem_G = new JRadioButton("ITEM");
		rdbtnItem_G.setSelected(true);
		buttonGroup_1.add(rdbtnItem_G);
		GridBagConstraints gbc_rdbtnItem_G = new GridBagConstraints();
		gbc_rdbtnItem_G.anchor = GridBagConstraints.WEST;
		gbc_rdbtnItem_G.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnItem_G.gridx = 1;
		gbc_rdbtnItem_G.gridy = 5;
		panel_2.add(rdbtnItem_G, gbc_rdbtnItem_G);
		
		rdbtnBlock_G = new JRadioButton("BLOCK");
		buttonGroup_1.add(rdbtnBlock_G);
		GridBagConstraints gbc_rdbtnBlock_G = new GridBagConstraints();
		gbc_rdbtnBlock_G.anchor = GridBagConstraints.WEST;
		gbc_rdbtnBlock_G.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnBlock_G.gridx = 2;
		gbc_rdbtnBlock_G.gridy = 5;
		panel_2.add(rdbtnBlock_G, gbc_rdbtnBlock_G);
		
		JButton btnSetToLifeless_G = new JButton("Set to Lifeless");
		btnSetToLifeless_G.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnBlock_G.isSelected()) {
					textField_G.setText(GameData.getBlockRegistry().getNameForObject(ChaosCrystalMain.blockLifeless));
				} else {
					textField_G.setText(GameData.getItemRegistry().getNameForObject(ChaosCrystalMain.itemLifelessShard));
				}
				spinner_G.setValue(0);
			}
		});
		GridBagConstraints gbc_btnSetToLifeless_G = new GridBagConstraints();
		gbc_btnSetToLifeless_G.anchor = GridBagConstraints.EAST;
		gbc_btnSetToLifeless_G.gridwidth = 4;
		gbc_btnSetToLifeless_G.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetToLifeless_G.gridx = 3;
		gbc_btnSetToLifeless_G.gridy = 5;
		panel_2.add(btnSetToLifeless_G, gbc_btnSetToLifeless_G);
		
		JLabel lblUniqueName_G = new JLabel("Unique Name");
		GridBagConstraints gbc_GblUniqueName_G = new GridBagConstraints();
		gbc_GblUniqueName_G.anchor = GridBagConstraints.EAST;
		gbc_GblUniqueName_G.insets = new Insets(0, 0, 5, 5);
		gbc_GblUniqueName_G.gridx = 0;
		gbc_GblUniqueName_G.gridy = 6;
		panel_2.add(lblUniqueName_G, gbc_GblUniqueName_G);
		
		textField_G = new JTextField();
		GridBagConstraints gbc_textField_G = new GridBagConstraints();
		gbc_textField_G.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_G.gridwidth = 2;
		gbc_textField_G.insets = new Insets(0, 0, 5, 5);
		gbc_textField_G.gridx = 1;
		gbc_textField_G.gridy = 6;
		panel_2.add(textField_G, gbc_textField_G);
		textField_G.setColumns(10);
		
		JLabel lblMeta_G = new JLabel("Meta");
		GridBagConstraints gbc_GblMeta_G = new GridBagConstraints();
		gbc_GblMeta_G.insets = new Insets(0, 0, 5, 5);
		gbc_GblMeta_G.gridx = 3;
		gbc_GblMeta_G.gridy = 6;
		panel_2.add(lblMeta_G, gbc_GblMeta_G);
		
		spinner_G = new JSpinner();
		spinner_G.setModel(new SpinnerNumberModel(Integer.valueOf(ItemUtil.WILDCARD_META), Integer.valueOf(0), null, Integer.valueOf(1)));
		spinner_G.setPreferredSize(new Dimension(80, 28));
		GridBagConstraints gbc_spinner_G = new GridBagConstraints();
		gbc_spinner_G.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_G.insets = new Insets(0, 0, 5, 5);
		gbc_spinner_G.gridx = 4;
		gbc_spinner_G.gridy = 6;
		panel_2.add(spinner_G, gbc_spinner_G);
		
		JButton btnWildcard_G = new JButton("*");
		btnWildcard_G.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinner_G.setValue(ItemUtil.WILDCARD_META);
			}
		});
		btnWildcard_G.setPreferredSize(new Dimension(29, 29));
		GridBagConstraints gbc_btnWildcard_G = new GridBagConstraints();
		gbc_btnWildcard_G.insets = new Insets(0, 0, 5, 5);
		gbc_btnWildcard_G.gridx = 5;
		gbc_btnWildcard_G.gridy = 6;
		panel_2.add(btnWildcard_G, gbc_btnWildcard_G);
		
		JButton btnBrowse_G = new JButton("...");
		btnBrowse_G.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String value = findBlockOrItemName(rdbtnBlock_G.isSelected() ? NodePointType.BLOCK : NodePointType.ITEM);
				if(value != null) {
					textField_G.setText(value);
				}
			}
		});
		btnBrowse_G.setPreferredSize(new Dimension(29, 29));
		GridBagConstraints gbc_btnBrowse_G = new GridBagConstraints();
		gbc_btnBrowse_G.insets = new Insets(0, 0, 5, 0);
		gbc_btnBrowse_G.gridx = 6;
		gbc_btnBrowse_G.gridy = 6;
		panel_2.add(btnBrowse_G, gbc_btnBrowse_G);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridwidth = 7;
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 7;
		panel_2.add(separator_1, gbc_separator_1);
		
		JPanel panel_3 = new JPanel();
		panel_3.setAutoscrolls(true);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.gridwidth = 7;
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 8;
		panel_2.add(panel_3, gbc_panel_3);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		for(int a = 0; a < Aspect.values().length; a++) {
			JLabel label = new JLabel("");
			label.setPreferredSize(new Dimension(16, 16));
			label.setIcon(new ImageIcon(ChaosCrystalAspectUtil.class.getResource("/assets/chaoscrystal/" + Aspect.RESOURCE_LOCATIONS[a].getResourcePath())));
			label.setToolTipText(Aspect.values()[a].stringRep);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 2 * (a / 4);
			gbc_label.gridy = a % 4;
			System.out.println(gbc_label.gridx + " - " + gbc_label.gridy);
			panel_3.add(label, gbc_label);
			aspectLabels[a] = label;
			
			JSpinner spinner = new JSpinner();
			GridBagConstraints gbc_spinner = new GridBagConstraints();
			gbc_spinner.insets = new Insets(0, 0, 5, 5);
			gbc_spinner.gridx = 2 * (a / 4) + 1;
			gbc_spinner.gridy = a % 4;
			panel_3.add(spinner, gbc_spinner);
			aspectSpinners[a] = spinner;
		}
		
		
		splitPane.setDividerLocation(150);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNewAspectModule = new JMenuItem("New Aspect Module");
		mntmNewAspectModule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadModule(new AspectModule());
			}
		});
		mnFile.add(mntmNewAspectModule);

		JMenuItem mntmLoadAspectModule = new JMenuItem("Load Aspect Module");
		mntmLoadAspectModule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().setVersion(1).disableHtmlEscaping().create();

					try {
						AspectModule newModule = gson.fromJson(new InputStreamReader(new FileInputStream(fileChooser.getSelectedFile()), Charsets.UTF_8), AspectModule.class);
						loadModule(newModule);
					} catch (JsonSyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JsonIOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		mnFile.add(mntmLoadAspectModule);

		JMenuItem mntmSaveAspectModule = new JMenuItem("Save Aspect Module");
		mntmSaveAspectModule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {

					saveModule(module);

					Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().setVersion(1).disableHtmlEscaping().create();

					FileOutputStream fos = null;
					OutputStreamWriter osw = null;
					try {
						fos = new FileOutputStream(fileChooser.getSelectedFile());
						osw = new OutputStreamWriter(fos, Charsets.UTF_8);
						gson.toJson(module, AspectModule.class, osw);
					} catch (JsonSyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JsonIOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} finally {
						if(osw != null) {
							try {
								osw.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						if(fos != null) {
							try {
								fos.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}

				}
			}
		});
		mnFile.add(mntmSaveAspectModule);

		fileChooser = new JFileChooser("assets/chaoscrystal/chaosregistry");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "json-FIles";
			}

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().toLowerCase().endsWith(".json");
			}
		});
	}

	private void loadDefs() {
		list.setModel(new ListModel() {

			private final List<ListDataListener> dataListeners = new ArrayList<ListDataListener>();

			@Override
			public void addListDataListener(ListDataListener listener) {
				dataListeners.add(listener);
			}
			
			@Override
			public void removeListDataListener(ListDataListener listener) {
				dataListeners.remove(listener);
			}

			@Override
			public int getSize() {
//				return registry.getDegradationNodeCount();
				return module.getNodes().size();
			}

			@Override
			public Object getElementAt(int index) {
//				return registry.getDegradationNode(index);
				return module.getNodes().get(index);
			}
		});
	}
	
	private String findBlockOrItemName(NodePointType type) {
		final JTextField filterField = new JTextField();
		final JCheckBox regexCB = new JCheckBox("RegEx", false);
		JScrollPane scrollPane = new JScrollPane();
		final JList list = new JList();
		scrollPane.setViewportView(list);
		
		FMLControlledNamespacedRegistry<?> registry = type == NodePointType.BLOCK ? GameData.getBlockRegistry() : GameData.getItemRegistry();
		@SuppressWarnings("unchecked")
		final Set<String> keys =  registry.getKeys();
		
		final DefaultListModel lm = new DefaultListModel();
		lm.ensureCapacity(keys.size());
		for(String key : keys) {
			lm.addElement(key);
		}
		list.setSelectedIndex(0);
		filterField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				refreshFilter();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				refreshFilter();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				refreshFilter();
			}
			
			private void refreshFilter() {
				String filter = filterField.getText();
				System.out.println(filter);
				lm.clear();
				if(filter.isEmpty()) {
					for(String key : keys) {
						lm.addElement(key);
					}
				} else {
					for(String key : keys) {
						if(regexCB.isSelected()) {
							try {
								if(key.matches(filter)) {
									lm.addElement(key);
								}
							} catch(PatternSyntaxException se) {
								if(key.startsWith(filter)) {
									lm.addElement(key);
								}
							}
						} else {
							if(key.contains(filter)) {
								lm.addElement(key);
							}
						}
						
					}
				}
				list.setSelectedIndex(0);
			}
		});
		list.setModel(lm);
		
		JPanel pan = new JPanel();
		
		BorderLayout bl = new BorderLayout();
		pan.setLayout(bl);
		pan.add(filterField, BorderLayout.NORTH);
		pan.add(scrollPane, BorderLayout.CENTER);
		
		int option = JOptionPane.showOptionDialog(frame, pan, type.name(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"OK", "Cancel"}, JOptionPane.OK_OPTION);
		if(option == JOptionPane.CANCEL_OPTION) {
			return null;
		} else {
			int idx = list.getSelectedIndex();
			if(idx == -1) {
				return null;
			} else {
				return (String)lm.getElementAt(idx);
			}
		}
	}

}
