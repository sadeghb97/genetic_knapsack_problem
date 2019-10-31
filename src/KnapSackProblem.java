import java.util.*;

public class KnapSackProblem {
    public static void start(String[] args) {
        int []w;
        int []v;
        int number;

        boolean isMinimal = false;
        String weightsFilePath;
        String valuesFilePath;
        int kw;

        if(isMinimal){
            weightsFilePath = Problem.minimalKnapsackWeightsFilePath;
            valuesFilePath = Problem.minimalKnapsackValuesFilePath;
            kw = Problem.minimalKnapsackMaxWeight;
        }
        else {
            weightsFilePath = Problem.mainKnapsackWeightsFilePath;
            valuesFilePath = Problem.mainKnapsackValuesFilePath;
            kw = Problem.mainKnapsackMaxWeight;
        }

        try {
            String weightsContents = UsefulUtils.readAllFile(weightsFilePath);
            String valuesContents = UsefulUtils.readAllFile(valuesFilePath);
            String[] weightPieces = weightsContents.split("\n");
            String[] valuesPieces = valuesContents.split("\n");

            number = weightPieces.length;
            number++;
            w=new int[number];
            v=new int[number];

            w[0] = 0;
            v[0] = 0;
            for(int i=1; number > i; i++){
                w[i] = Integer.valueOf(weightPieces[i-1].trim());
                v[i] = Integer.valueOf(valuesPieces[i-1].trim());
            }

            System.out.println();
            for(int i=0; number>i; i++){
                System.out.printf("Item %d | W: %d | V: %d\n", i,w[i],v[i]);
            }

            int sw=0, sv=0;
            for(int i=0; number>i; i++){
                sw+=w[i];
                sv+=v[i];
            }

            System.out.printf("\nSum of Weight: %d | Sum of Values: %d\n",sw,sv);

            System.out.println();
            System.out.printf("Max Value: %d\n", bottomUpKP(w, v, number, kw));
        }
        catch (Exception ex){
            System.out.println("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static int bottomUpKP(int w[],int v[],int n,int kw){
        int op[][]=new int[kw+1][n];
        for(int i=1; kw+1>i; i++){
            for(int j=0; n>j; j++){
                if(i==0 || j==0) op[i][j]=0;
                else op[i][j]=-1;
            }
        }

        int [][]selectedItems=new int[kw+1][n];
        for(int i=0; kw+1>i; i++) for(int j=0; n>j; j++) selectedItems[i][j]=0;
        
        for(int i=1; n>i; i++){
            for(int j=1; kw>=j; j++){
                if(w[i]>j){
                    op[j][i]=op[j][i-1];
                    selectedItems[j][i]=selectedItems[j][i-1];
                    continue;
                }
                
                int p=op[j][i-1];
                int q=op[j-w[i]][i-1]+v[i];
                if(p>q){
                    op[j][i]=p;
                    selectedItems[j][i]=selectedItems[j][i-1];
                }
                else{
                    op[j][i]=q;
                    selectedItems[j][i]=i;
                }
            }
        }
        
        printItems(selectedItems, w, kw, n);
        return op[kw][n-1];
    }
    
    public static void printItems(int items[][],int w[],int kw,int n){
        boolean first=true;
        System.out.println("Selected Items:");
        int lastItem=items[kw][n-1];
        while(lastItem>0){
            if(!first) System.out.printf("-");
            first=false;
            System.out.printf("%d",lastItem);
            kw-=w[lastItem];
            lastItem=items[kw][lastItem-1];
        }
        System.out.println("\n");
    }
}
