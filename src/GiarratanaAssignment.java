//package data_engineer_assignment_prj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GiarratanaAssignment {

	static final int NUM_REG_ITA = 20;
	static final String CSV_SEPARATOR = ";";
	static final String DECIMAL_SEPARATOR = ",";	
	static Object[][] regionVote; //matrix containing summary values for i-th region
	static Object[][] output; //matrix containing output values for i-th region
	static String inputCsvPath, outputCsvPath;
	
	
	public static void main(String[] args) {
		
		try {
			if( args.length > 0 ) {
				File f = new File(args[0]);
				if(f.exists() && !f.isDirectory()) { 
					inputCsvPath = args[0];
					outputCsvPath = inputCsvPath.replace(".csv","-aggregated.csv");
					readAndCleanCSV(inputCsvPath);				
					sumData();
					writeCsv(outputCsvPath);					
					System.out.println("Output file was written to " + outputCsvPath);
					}
			 else {
			    System.err.println("ERROR - Missing or invalid input file");
			 }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("ERROR - Unhandled exception:\n");			
			e.printStackTrace();
		}				
	}

	
	/* reads data from CSV file, skips invalid data, sums valid data into regionVote variable*/
	public static void readAndCleanCSV(String inputCsvPath) {
		
		BufferedReader br = null;
		String line = "", curRegion = "";
		boolean headerFound = false;
		int csvLine = 0;		
		int regionCounter = -1, eletM, elet, votanti, votantiSi, votantiNo, bianche, nonValide, contestate;
		regionVote = new Object[NUM_REG_ITA][8];
		try {
			br = new BufferedReader(new FileReader(inputCsvPath));
			while ((line = br.readLine()) != null) {
				csvLine++;
				if (headerFound) { // skip first line (headers)
					String[] townVote = line.split(CSV_SEPARATOR); // split row into separated columns
					if (townVote[0].trim().equalsIgnoreCase("ABRUZZO")
							|| townVote[0].trim().equalsIgnoreCase("BASILICATA")
							|| townVote[0].trim().equalsIgnoreCase("CALABRIA")
							|| townVote[0].trim().equalsIgnoreCase("CAMPANIA")
							|| townVote[0].trim().equalsIgnoreCase("EMILIA-ROMAGNA")
							|| townVote[0].trim().equalsIgnoreCase("FRIULI-VENEZIA GIULIA")
							|| townVote[0].trim().equalsIgnoreCase("LAZIO")
							|| townVote[0].trim().equalsIgnoreCase("LIGURIA")
							|| townVote[0].trim().equalsIgnoreCase("LOMBARDIA")
							|| townVote[0].trim().equalsIgnoreCase("MARCHE")
							|| townVote[0].trim().equalsIgnoreCase("MOLISE")
							|| townVote[0].trim().equalsIgnoreCase("PIEMONTE")
							|| townVote[0].trim().equalsIgnoreCase("PUGLIA")
							|| townVote[0].trim().equalsIgnoreCase("SARDEGNA")
							|| townVote[0].trim().equalsIgnoreCase("SICILIA")
							|| townVote[0].trim().equalsIgnoreCase("TOSCANA")
							|| townVote[0].trim().equalsIgnoreCase("TRENTINO-ALTO ADIGE")
							|| townVote[0].trim().equalsIgnoreCase("UMBRIA")
							|| townVote[0].trim().equalsIgnoreCase("VALLE D'AOSTA")
							|| townVote[0].trim().equalsIgnoreCase("VENETO")) { //check region name 

						if (!(curRegion.equalsIgnoreCase(townVote[0].trim()))) { // if
																														// a
																														// new
																														// (valid)
																														// region
																														// is
																														// found
																														// a
																														// new
																														// vector
																														// is
																														// initialized
							
							regionCounter++;
							regionVote[regionCounter][0] = townVote[0].trim();
							regionVote[regionCounter][1] = 0;
							regionVote[regionCounter][2] = 0;
							regionVote[regionCounter][3] = 0;
							regionVote[regionCounter][4] = 0;
							regionVote[regionCounter][5] = 0;
							regionVote[regionCounter][6] = 0;
							regionVote[regionCounter][7] = 0;							
						}
					
						try {
							eletM = Integer.parseInt(townVote[4]);
							elet = Integer.parseInt(townVote[3]);
							votanti = Integer.parseInt(townVote[5]);
							votantiSi = Integer.parseInt(townVote[7]);
							votantiNo = Integer.parseInt(townVote[8]);
							bianche = Integer.parseInt(townVote[9]);
							nonValide = Integer.parseInt(townVote[10]);
							contestate = Integer.parseInt(townVote[11]);

							if ((eletM >= 0) && (elet >= eletM) && (elet >= 0) && (votanti >= 0) && (votanti >= votantiSi)
									&& (votanti >= votantiNo) && (votantiSi >= 0) && (votantiNo >= 0) && (bianche >= 0)
									&& (nonValide >= 0) && (contestate >= 0)) {
								regionVote[regionCounter][1] = (int) regionVote[regionCounter][1] + eletM; // elettori maschi
								regionVote[regionCounter][2] = (int) regionVote[regionCounter][2] + elet - eletM; // elettori  femmine
								regionVote[regionCounter][3] = (int) regionVote[regionCounter][3] + elet; // elettori totali
								regionVote[regionCounter][4] = (int) regionVote[regionCounter][4] + votanti; // votanti
								regionVote[regionCounter][5] = (int) regionVote[regionCounter][5] + votantiSi; // voti si
								regionVote[regionCounter][6] = (int) regionVote[regionCounter][6] + votantiNo; // voti no
								regionVote[regionCounter][7] = (int) regionVote[regionCounter][7] + bianche + nonValide
										+ contestate; // bianche, non valide o contestate							
								
							}
							else
							{
								System.err.println("*** WARNING - Invalid data in line " + csvLine);
							}

						} catch (NumberFormatException e) {
							System.err.println("*** WARNING - Invalid number format in line " + csvLine);
						}
						curRegion = townVote[0].trim();
					} else
						System.err.println(
								"*** WARNING - Invalid region name in line " + csvLine);

				} else
					headerFound = true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		

	}	

    /* Calculates percentages and sums cleaned data into output variable*/
	public static void sumData() {
		output = new Object[NUM_REG_ITA][8];
			
		for (int i=0; i < NUM_REG_ITA; i++) {
			output[i][0] = regionVote[i][0]; //nome regione
		    output[i][1] = regionVote[i][1]; //Elettori Maschi
		    output[i][2] = regionVote[i][2]; //Elettori Femmine	    
		    
		    output[i][3] = regionVote[i][3]; //Elettori totali
		    		    
		    output[i][4] = Float.parseFloat(regionVote[i][4].toString()) / (int) regionVote[i][3]; //Percentuali votanti
		    
		    output[i][5] = Float.parseFloat(regionVote[i][5].toString()) / Float.parseFloat(regionVote[i][4].toString()); //Percentuali voti sì
		    output[i][6] = Float.parseFloat(regionVote[i][6].toString()) / Float.parseFloat(regionVote[i][4].toString()); //Percentuali voti no
		    output[i][7] = Float.parseFloat(regionVote[i][7].toString()) / Float.parseFloat(regionVote[i][4].toString()); //Percentuale schede bianche, non valide o contestate.
		    
		}
		
	}

	/* Writes output variable to CSV output file*/
	public static void writeCsv(String outputCsvPath) {
		
		String line;
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(
					outputCsvPath));		
			
			line =			
					"Regione"+ CSV_SEPARATOR + "Elettori Maschi" + CSV_SEPARATOR + "Elettori Femmine" + CSV_SEPARATOR + "Elettori Totali" + CSV_SEPARATOR
					+ "Percentuali votanti" + CSV_SEPARATOR + "Percentuale voti sì" + CSV_SEPARATOR + "Percentuale voti no" + CSV_SEPARATOR + "Percentuale schede bianche, non valide o contestate";
					writer.write(line);
		            writer.newLine();

		for (int i=0; i < NUM_REG_ITA; i++) {
			line = (output[i][0] + CSV_SEPARATOR +
					output[i][1] + CSV_SEPARATOR +
					output[i][2] + CSV_SEPARATOR +
					output[i][3] + CSV_SEPARATOR +
					output[i][4] + CSV_SEPARATOR +
					output[i][5] + CSV_SEPARATOR +
					output[i][6] + CSV_SEPARATOR +
					output[i][7]).replace(".", DECIMAL_SEPARATOR);	    
			
			writer.write(line);
            writer.newLine();
		    
		} 
        writer.close();
		
		} catch (IOException e) {
			System.err.println("ERROR - cannot write to output file");
		}		
	}






}