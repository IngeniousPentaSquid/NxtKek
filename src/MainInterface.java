import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.imgscalr.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.SystemColor;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument.Iterator;

public class MainInterface {

	private JFrame frame;
	public static JTextField statusField;
	private JTextField scaleField;
	private JTextField cropX;
	private JTextField cropY;
	private ArrayList<Preset> presets;
	private int imageAt = 0;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainInterface window = new MainInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Load the presets from the text file
	 */
	private void readPresets() {
		// String[]
		try {
			presets = new ArrayList<Preset>();
			FileReader reader = new FileReader("presets.txt");
			BufferedReader bReader = new BufferedReader(reader);
			String currentLine;
			while ((currentLine = bReader.readLine()) != null) {
				String[] preset = currentLine.split(" ");
				String name = preset[0];
				int thumbX = Integer.parseInt(preset[1]);
				int thumbY = Integer.parseInt(preset[2]);
				int storyX = Integer.parseInt(preset[3]);
				int storyY = Integer.parseInt(preset[4]);
				int windowX = Integer.parseInt(preset[5]);
				int windowY = Integer.parseInt(preset[6]);
				presets.add(new Preset(name, thumbX, thumbY, storyX, storyY, windowX, windowY));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Preset p : presets) {
			System.out.println(p);
		}
	}

	/**
	 * Create the application.
	 */
	public MainInterface() {
		initialize();
	}
	
	/**
	 * Helper method for finding a preset, given its name
	 * @param presetName
	 * @return
	 */
	public Preset findPreset(String presetName) {
		for (Preset p : presets) {
			if (p.getName().equals(presetName)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("unchecked")
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.DARK_GRAY);
		frame.getContentPane().setForeground(Color.DARK_GRAY);
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 603, 653);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//Load presets
		readPresets();

		final JFileChooser fc = new JFileChooser();

		final JComboBox magBox = new JComboBox();
		magBox.setBounds(190, 60, 202, 27);

		JLabel lblNewLabel = new JLabel("Magazine");
		lblNewLabel.setBounds(251, 32, 68, 16);

		final Scaling scale = new Scaling();

		final JLabel storyLabel = new JLabel("");

		final JLabel thumbLabel = new JLabel("");
		thumbLabel.setBounds(404, 248, 140, 140);
		
		//Populate the drop down menu
		for (Preset p : presets) {
			magBox.addItem(p.getName());
		}
		
		//Handler for the image chooser
		JButton btnChooseImage = new JButton("Pick Images");
		btnChooseImage.setBounds(480, 541, 117, 29);
		btnChooseImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setMultiSelectionEnabled(true);
				int returnVal = fc.showOpenDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					String selectedItem = (String) magBox.getSelectedItem();
					File[] files = fc.getSelectedFiles();
					scale.cleanUp();
					for (File f : files) {
						scale.addFiles(f);
						statusField.setText("Opening: " + f.getName());
					}
					scale.createBImages();
					imageAt = 0;
					System.out.println(scale.bImages.size());
					for (BufferedImage bi : scale.bImages) {
						Preset selectedPreset = findPreset(selectedItem);
						scale.resizeStory(bi, selectedPreset.getStoryX(), selectedPreset.getStoryY());
						storyLabel.setIcon(new ImageIcon(scale.storyImages.get(0)));
						storyLabel.setSize(new Dimension(selectedPreset.getStoryX(), selectedPreset.getStoryY()));
						statusField.setText(scale.storedImages.get(imageAt).getName());
					}
				} else {
					statusField.setText("Choose Image Cancelled");
				}
			}
		});

		statusField = new JTextField();
		statusField.setBounds(6, 595, 235, 28);
		statusField.setEditable(false);
		statusField.setColumns(10);

		thumbLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//handler for saving the thumbnail
		JButton btnSaveThumb = new JButton("Save Thumb");
		btnSaveThumb.setBounds(253, 596, 120, 29);
		btnSaveThumb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".jpg");
					statusField.setText(fc.getSelectedFile().getName() + ".jpg");
					try {
						ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
						ImageWriteParam iwp = writer.getDefaultWriteParam();
						iwp.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
						iwp.setCompressionQuality(1); 
						FileImageOutputStream output = new FileImageOutputStream(file);
						writer.setOutput(output);
						IIOImage image = new IIOImage(scale.getThumbImage(), null, null);
						writer.write(null, image, iwp);
						writer.dispose();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					statusField
							.setText("Writing to: " + fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + "png");
				}
			}
		});
		
		//handler for saving the main image
		JButton btnSaveStory = new JButton("Save Main");
		btnSaveStory.setBounds(372, 596, 108, 29);
		btnSaveStory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".jpg");
					statusField.setText(fc.getSelectedFile().getName() + ".jpg");
					try {
						
						ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
						ImageWriteParam iwp = writer.getDefaultWriteParam();
						JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
						jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
						jpegParams.setCompressionQuality(1.0f);
						FileImageOutputStream output = new FileImageOutputStream(file);
						writer.setOutput(output);
						IIOImage image = new IIOImage(scale.storyImages.get(imageAt), null, null);
						writer.write(null, image, jpegParams);
						writer.dispose();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					statusField
							.setText("Writing to: " + fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName());
				}
			}
		});
		
		//Shows the aspect ratio
		JLabel ratioLabel = new JLabel("Scale Ratio");
		ratioLabel.setBounds(10, 546, 68, 16);

		scaleField = new JTextField();
		scaleField.setBounds(6, 567, 86, 28);
		scaleField.setColumns(10);

		JLabel lblCropX = new JLabel("Crop X");
		lblCropX.setBounds(113, 546, 42, 16);

		cropX = new JTextField();
		cropX.setBounds(118, 567, 37, 28);
		cropX.setText("0");
		cropX.setColumns(10);
		
		//Handler for the crop button
		JButton cropButton = new JButton("Crop");
		cropButton.setBounds(253, 568, 120, 29);
		cropButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = (String) magBox.getSelectedItem();
				Preset selectedPreset = findPreset(selectedItem);
//				scale.cropImage(Integer.parseInt(cropX.getText()), Integer.parseInt(cropX.getText()),
//						selectedPreset.getStoryX(), selectedPreset.getStoryY(), 0);
//				storyLabel.setIcon(new ImageIcon(scale.getStoryImage()));
			}
		});
		
		//Handler for the thumbnail crop button
		JButton btnCropT = new JButton("Crop Thumb");
		btnCropT.setBounds(372, 568, 108, 29);
		btnCropT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				scale.cropImage(Integer.parseInt(cropX.getText()), Integer.parseInt(cropY.getText()), 140, 140, 1);
//				thumbLabel.setIcon(new ImageIcon(scale.getThumbImage()));
			}
		});
		
		JButton btnSetMag = new JButton("Set Mag");
		btnSetMag.setBounds(480, 568, 117, 29);
		btnSetMag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedItem = (String) magBox.getSelectedItem();
				Preset selectedPreset = findPreset(selectedItem);
				storyLabel.setSize(new Dimension(selectedPreset.getStoryX(), selectedPreset.getStoryY()));
				thumbLabel.setSize(new Dimension(selectedPreset.getThumbX(), selectedPreset.getThumbY()));
				frame.setSize(new Dimension(selectedPreset.windowX(), selectedPreset.windowY()));
				//frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(thumbLabel);
		frame.getContentPane().add(ratioLabel);
		frame.getContentPane().add(scaleField);
		frame.getContentPane().add(lblCropX);
		frame.getContentPane().add(cropButton);
		frame.getContentPane().add(btnCropT);
		frame.getContentPane().add(cropX);

		storyLabel.setLocation(63, 130);
		frame.getContentPane().add(storyLabel);
		storyLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		storyLabel.setSize(new Dimension(300, 400));
		frame.getContentPane().add(lblNewLabel);
		frame.getContentPane().add(magBox);
		frame.getContentPane().add(btnSetMag);
		frame.getContentPane().add(btnChooseImage);
		frame.getContentPane().add(statusField);
		frame.getContentPane().add(btnSaveThumb);
		frame.getContentPane().add(btnSaveStory);

		JLabel lblCropY = new JLabel("Crop Y");
		lblCropY.setBounds(189, 546, 61, 16);
		frame.getContentPane().add(lblCropY);

		cropY = new JTextField();
		cropY.setText("0");
		cropY.setBounds(189, 567, 37, 28);
		frame.getContentPane().add(cropY);
		cropY.setColumns(10);
		
		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storyLabel.setIcon(new ImageIcon(scale.storyImages.get(imageAt + 1)));
				imageAt++;
				statusField.setText(scale.storedImages.get(imageAt).getName());
			}
		});
		btnNext.setBounds(253, 541, 120, 29);
		frame.getContentPane().add(btnNext);
		
		JButton btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				storyLabel.setIcon(new ImageIcon(scale.storyImages.get(imageAt - 1)));
				imageAt--;
				statusField.setText(scale.storedImages.get(imageAt).getName());
			}
		});
		btnPrevious.setBounds(372, 541, 108, 29);
		frame.getContentPane().add(btnPrevious);
		
		JButton btnSaveAll = new JButton("Save All");
		btnSaveAll.setBounds(480, 596, 117, 29);
		frame.getContentPane().add(btnSaveAll);
		
		JButton btnSavePng = new JButton("Save PNG");
		btnSavePng.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showSaveDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = new File(fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName() + ".png");
					statusField.setText(fc.getSelectedFile().getName() + ".png");
					try {
						
					    final String formatName = "png";

					       ImageWriter writer = ImageIO.getImageWritersByFormatName(formatName).next();
					       ImageWriteParam writeParam = writer.getDefaultWriteParam();
					       ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
					       IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

					       setDPI(metadata);

					       final ImageOutputStream stream = ImageIO.createImageOutputStream(file);
					       try {
					          writer.setOutput(stream);
					          writer.write(metadata, new IIOImage(scale.storyImages.get(imageAt), null, metadata), writeParam);
					       } finally {
					          stream.close();
					       }
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					statusField
							.setText("Writing to: " + fc.getCurrentDirectory() + "/" + fc.getSelectedFile().getName());
				}		
			}
		});
		btnSavePng.setBounds(480, 514, 117, 29);
		frame.getContentPane().add(btnSavePng);

	}
	
	private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

	    // for PMG, it's dots per millimeter
	    double dotsPerMilli = 1.0 * 300 / 10 / 2.54;

	    IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
	    horiz.setAttribute("value", Double.toString(dotsPerMilli));

	    IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
	    vert.setAttribute("value", Double.toString(dotsPerMilli));

	    IIOMetadataNode dim = new IIOMetadataNode("Dimension");
	    dim.appendChild(horiz);
	    dim.appendChild(vert);

	    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
	    root.appendChild(dim);

	    metadata.mergeTree("javax_imageio_1.0", root);
	 }
}
