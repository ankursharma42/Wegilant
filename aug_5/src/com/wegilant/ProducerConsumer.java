package com.wegilant;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * This is the main class of the problem
 */
public class ProducerConsumer extends JFrame {

	JTextArea produced = new JTextArea();
	JTextArea consumed = new JTextArea();

	public ProducerConsumer() {
		super("Producer Consumer");

		FileProcessor fileProcessor = new FileProcessor();
		fileProcessor.readLocalFile(FileProcessor.localFileLocation);
		fileProcessor.readRemoteFile();
		// merging both the files
		fileProcessor.mergefile(FileProcessor.mregedFileLocation);
		//creating the UI
		JLabel producerLabel = new JLabel("Producer Panel");
		
		JLabel consumerLabel = new JLabel("Consumer Panel");
		
		JTextArea produced = new JTextArea(100, 100);
		JTextArea consumed = new JTextArea(100, 100);
		JButton buttonStart = new JButton("START");

		JPanel producerConsumerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.gridx = 0;
		constraints.gridy = 0;
		producerConsumerPanel.add(producerLabel, constraints);
		constraints.gridx = 1;
		producerConsumerPanel.add(consumerLabel, constraints);
		constraints.gridx = 0;
		constraints.gridy = 1;
		producerConsumerPanel.add(this.produced, constraints);
		constraints.gridx = 1;
		producerConsumerPanel.add(this.consumed, constraints);
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		producerConsumerPanel.add(buttonStart, constraints);
		producerConsumerPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Producer Consumer"));
		add(producerConsumerPanel);
		pack();
		setLocationRelativeTo(null);
		setSize(1000, 600);
		buttonStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				initiateStart();
			}
		});
	}
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ProducerConsumer().setVisible(true);
			}
		});
	}

	public void initiateStart() {
		
		try {
			// Producer thread. Producer thread count
			for (int i = 0; i < 2; i++) {
				Producer myThread = new Producer() {
					public void run() {
						super.run();
						try {
							File fXmlFile = new File(FileProcessor.mregedFileLocation);
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory
									.newInstance();
							DocumentBuilder dBuilder = dbFactory
									.newDocumentBuilder();
							Document doc = dBuilder.parse(fXmlFile);
							doc.getDocumentElement().normalize();
							NodeList iList = doc
									.getElementsByTagName(FileProcessor.childElement1);
							if (iList == null) {
								System.out
										.println("Error in generating merged file");
								System.exit(0);
							}
							for (int temp = 0; temp < iList.getLength(); temp++) {

								Node nNode = iList.item(temp);
								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eElement = (Element) nNode;
									synchronized (ProducerPanel.class) {
										if (ProducerPanel.isFull()) {
											System.out
													.println("The Queue is full cannot add more items");
										} else {
											if (!ProducerPanel.present(eElement
													.getTextContent().trim())) {
												System.out
														.println("Item : "
																+ eElement
																		.getTextContent()
																		.trim()
																+ "producer"
																+ this.getName());
												ProducerPanel.enqueue(eElement
														.getTextContent()
														.trim());
												produced.setText(ProducerPanel
														.print().toString());
												sleep(500);
											}
										}
									}

								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(0);
						}

					}
				};
				myThread.start();
				myThread.setName("Producer " + i);
				myThread.setPriority(5);
			}
			//consumer thread count
			for (int i = 0; i < 3; i++) {
				Consumer myThread = new Consumer() {
					public void run() {
						super.run();
						//long i = 0;
						try {
							while (true) { //replacing i<10000000 with true
								synchronized (ProducerPanel.class) {
									if (!ProducerPanel.hasItems()) {
									} else {
										System.out
												.println("Dequeuing from the queue by thread"
														+ this.getName());
										String fetchedElement = ProducerPanel
												.dequeue();
										produced.setText(ProducerPanel.print()
												.toString());
										System.out.println("Item Dequeued: "
												+ fetchedElement);
										String reverse = new StringBuffer(
												fetchedElement).reverse()
												.toString();
										ConsumerPanel.enqueue(reverse);
										consumed.setText(ConsumerPanel.print()
												.toString());
										sleep(1000);
									}

								}
								//i++;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				myThread.start();
				myThread.setName("Consumer " + i);
				myThread.setPriority(5);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
