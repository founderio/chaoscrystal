package founderio.chaoscrystal.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import founderio.chaoscrystal.aspects.AspectModule;
import founderio.chaoscrystal.aspects.ChaosRegistry;

public class ChaosCrystalAspectUtil {

	private final ChaosRegistry registry;
	private AspectModule module = new AspectModule();
	
	private JFrame frame;
	private JList list;
	private JTextField txtModuleName;
	private JFileChooser fileChooser;
	/**
	 * Launch the application.
	 */
	public static void open(final ChaosRegistry registry) {
		EventQueue.invokeLater(new Runnable() {
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
		txtModuleName.setText(module.getName());
	}
	//TODO: File paths, handle saving & loading directly in here.
	
	public void saveModule(AspectModule module) {
		module.setName(txtModuleName.getText());
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerSize(5);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		list = new JList();
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
		splitPane.setDividerLocation(150);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewAspectModule = new JMenuItem("New Aspect Module");
		mntmNewAspectModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadModule(new AspectModule());
			}
		});
		mnFile.add(mntmNewAspectModule);
		
		JMenuItem mntmLoadAspectModule = new JMenuItem("Load Aspect Module");
		mntmLoadAspectModule.addActionListener(new ActionListener() {
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
			public void removeListDataListener(ListDataListener listener) {
				dataListeners.remove(listener);
			}
			
			@Override
			public int getSize() {
				return registry.getDegradationNodeCount();
			}
			
			@Override
			public Object getElementAt(int index) {
				return registry.getDegradationNode(index);
			}
			
			@Override
			public void addListDataListener(ListDataListener listener) {
				dataListeners.add(listener);
			}
		});
	}

}
