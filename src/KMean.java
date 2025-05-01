import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.io.*;
import java.util.*;
public class KMean {
	static ArrayList<data>database=new ArrayList<>();
	public static void main(String []args) {
		Scanner sc=new Scanner(System.in);
		System.out.println("where is your excel file?");
		String path=sc.next();
		System.out.println("and how much is your features?");
		int feat=sc.nextInt();
		System.out.println("tell me how much group do you want");
		int group=sc.nextInt();
		data[]M=k_mean(path,feat,group);
        for(int i=0;i<M.length;i++) 
        	System.out.println("group "+(i+1)+" : "+Arrays.toString(M[i].feature));
	}
    public static data[] k_mean(String filePath,int feat,int group) {
    	double [][]rewrite=new double[10000000][feat];
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            	data d=new data(new double[feat]);
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    for (int colIndex = 0; colIndex < row.getPhysicalNumberOfCells(); colIndex++) {
                    	Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                                case NUMERIC:
                                    d.feature[colIndex]=cell.getNumericCellValue();
                                    rewrite[rowIndex][colIndex]=cell.getNumericCellValue();
                                    break;
                                default:
                                    System.out.println("wrong data:" + cell.toString());
                            }
                        }
                    }
                    database.add(d);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        data[] dataBaseArr=new data[database.size()];
        {//Trans ArrayList to Array
        	int i=0;
        	for(data d:database) {
        		dataBaseArr[i]=d;
        		i++;
        	}
        }
        //K-mean
        Map<data,ArrayList<data>>saveGroup=new HashMap<>();
        data[]midpoint=new data[group];
        for(int i=0;i<group;i++) 
        	midpoint[i]=new data(dataBaseArr[i].feature);
        //loop start from here
        boolean keep=true;
        for(int looptime=0;keep&&looptime<=600000;looptime++) {
        	keep=false;
        for(data d:dataBaseArr) {
    		if(d.closerData==null)
    			d.closerData=midpoint[0];
        	for(int i=0;i<group;i++) 
        		if(d.distance(midpoint[i])<=d.distance(d.closerData)) {
        			d.closerData=midpoint[i];
        		}
        }//set all data closest point
        for(data d:midpoint)
        	saveGroup.put(d,new ArrayList<data>());
        for(data d:dataBaseArr) 
        	saveGroup.get(d.closerData).add(d);
        //refrash the group
        for(data d:midpoint) {
        	double[]total=new double[feat];
        	for(data ele:saveGroup.get(d)) {
        		for(int i=0;i<feat;i++)
        			total[i]+=ele.feature[i];
        	}
        	for(int i=0;i<feat;i++) {
        		total[i]/=(double)feat;
        		if(d.feature[i]!=total[i])
        			keep=true;
        		d.feature[i]=total[i];
        	}
        }
        //refrash the group middle point
        }
        {
        char c='1';
        for(data d:midpoint) {
        	for(int e=0;e<saveGroup.get(d).size();e++)
        		System.out.println(Arrays.toString(rewrite[e])+" is belong to group "+c);
        	c++;
        }
        }
        return midpoint;
    }
}
class data{
	public double [] feature;
	protected data closerData;
	public double distance(data d) {
		double dis=0;
		for(int i=0;i<d.feature.length;i++) {
			dis+=(d.feature[i]-this.feature[i])*(d.feature[i]-this.feature[i]);
		}
		return Math.pow(dis,1.0/d.feature.length);
	}
	public data(double []arr) {
		this.feature=arr;
	}
}
