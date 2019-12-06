package projet_algav;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class Main_algav {

	public static void main(String[] args) {
		File file = new File("../samples/test-1254.points");
		ArrayList<Point> points = new ArrayList<Point>() ;
		try {//Extraction des données
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st;
			while((st = br.readLine())!= null) {
				String values[] = st.split(" ");
				points.add(new Point(Integer.valueOf(values[0]),Integer.valueOf(values[1])));
				
			}
			ArrayList<Point> filtre = filtrage_alk_toussaint(points);
			System.out.println("avant filtrage :"+points.size()+" apres le filtrage "+filtre.size());
			JFrame frame = new JFrame("test points affichage");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
			ArrayList<Line> rectanglemin = rectangleMinimal(points);
			
			frame.add(new MyPanel(filtre,rectanglemin));
			frame.pack();
			frame.setVisible(true);
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ArrayList<Point> filtrage_alk_toussaint(ArrayList<Point> pts){
		int n = pts.size();
		
        if (n<4) return pts;

        Point ouest = pts.get(0);
        Point sud = pts.get(0);
        Point est = pts.get(0);
        Point nord = pts.get(0);
        for (Point p: pts){
        	//Ici on applique un filtre pour réduire le nombre de points que nous aurons à traiter plus tard
            if (p.x<ouest.x) ouest=p;
            if (p.y>sud.y) sud=p;
            if (p.x>est.x) est=p;
            if (p.y<nord.y) nord=p;
        }//A la sortie de la boucle nous obtenons quatre points formant les quatres extremums de notre ensemble de points
        ArrayList<Point> result = (ArrayList<Point>)pts.clone();
        for (int i=0;i<result.size();i++) {
            if (triangleContientPoint(ouest,sud,est,result.get(i)) ||
                    triangleContientPoint(ouest,est,nord,result.get(i))) {
                result.remove(i);
                i--;
            }

        }//A la sortie de cette boucle, nous avons bien supprimé tous les points contenus dans les deux triangles 
        //c'est à dire le quadrilatère formé par nos quatre points.
        //Cependant, il est fortement possible que nous ayons plus de quatre points dans notre enveloppes convexes.
        //Or nous voulons obtenir le rectangle minimum
        //Filtrage Alk-Toussaint effectué
        
        return result;
	}
	
	public static ArrayList<Line> rectangleMinimal(ArrayList<Point> points){
		//On récupère les points après filtrage de Alk-Toussaint	
		ArrayList<Line> result = new ArrayList<Line>();
		//filtrage_alk_toussaint ne renvoie pas une liste triée
		ArrayList<Point> p = filtrage_alk_toussaint(points);
		//On trie liste suivante x puis y si x identiques
		Collections.sort(p,new Comparator<Point>() {
				@Override
				public int compare(final Point p1, Point p2) {
					if(p1.getX()<p2.getX()) {
						return 1;
					}
					else if(p1.getX()>p2.getX()) return -1;
					else 
						if(p1.getY()<p2.getY())
							return 1;
						else if (p1.getY()>p2.getY()) return -1;
						else return 0;
				}
		});
		ArrayList<Line> antipodales = new ArrayList<Line>();
		int n = p.size();
		int k = 1;
		while (distance(p.get(k),p.get(n-1),p.get(0)) < distance(p.get((k+1)%n),p.get(n-1),p.get(0))) k++;
		//on a k l'indice du point tel que sa distance avec la droite 0 et n-1 est moins grande que pour le point k+1
		int i = 0;
		int j = k;
		while (i<=k && j<n) {
            while (distance(p.get(j),p.get(i),p.get(i+1))<distance(p.get((j+1)%n),p.get(i),p.get(i+1)) && j<n-1) {
                antipodales.add(new Line(p.get(i),p.get(j)));
                j++;
            }
            antipodales.add(new Line(p.get(i),p.get(j)));
            i++;
        }
		//Ici j'ai récupéré toutes les paires antipodales
		Line l1 = antipodales.get(0);
		Line l2 = antipodales.get(antipodales.size()-1);
		//si le produit vectoriel nous donne quelque chose de positif alors on est dans le bon sens
		//on ajoute la droite passant par le premier point de chaque segment
		//sinon on a deux droites qui s'intersectent 
		//on créé deux segments pour obtenir un rectangle
		Line l3,l4;
		System.out.println("nombre lines antipodales : "+antipodales.size());
		if(crossProduct(l1.getP1(),l1.getP2(),l2.getP1(),l2.getP2())>0) {
			 l3 = new Line(l1.getP1(),l2.getP1());
			 l4 = new Line(l1.getP2(),l2.getP2());
		}
		else {
			 l3 = new Line(l1.getP1(),l2.getP2());
			 l4 = new Line(l1.getP2(),l2.getP1());
		}
		result.add(l1);
		result.add(l2);
		result.add(l3);
		result.add(l4);
		//result contient notre rectangle minimum
		return antipodales;
		
	}
    private static boolean triangleContientPoint(Point a, Point b, Point c, Point x) {
        double l1 = ((b.y-c.y)*(x.x-c.x)+(c.x-b.x)*(x.y-c.y))/(double)((b.y-c.y)*(a.x-c.x)+(c.x-b.x)*(a.y-c.y));
        double l2 = ((c.y-a.y)*(x.x-c.x)+(a.x-c.x)*(x.y-c.y))/(double)((b.y-c.y)*(a.x-c.x)+(c.x-b.x)*(a.y-c.y));
        double l3 = 1-l1-l2;
        return (0<l1 && l1<1 && 0<l2 && l2<1 && 0<l3 && l3<1);
    }
    
    private static double distance(Point p, Point a, Point b) {
        return Math.abs(crossProduct(a,b,a,p));
    }
    
    
    private static double crossProduct(Point p, Point q, Point s, Point t){
        return ((q.x-p.x)*(t.y-s.y)-(q.y-p.y)*(t.x-s.x));
    }
    
    //methode naive 
    private static ArrayList<Point> tme6exercice1(ArrayList<Point> points){
        if (points.size()<4) return points;

        ArrayList<Point> enveloppe = new ArrayList<Point>();

        for (Point p: points) {
            for (Point q: points) {
                if (p.equals(q)) continue;
                Point ref=p;
                for (Point r: points) if (crossProduct(p,q,p,r)!=0) {ref=r;break;}
                if (ref.equals(p)) {enveloppe.add(p); enveloppe.add(q); continue;}
                double signeRef = crossProduct(p,q,p,ref);
                boolean estCote = true;
                for (Point r: points) if (signeRef * crossProduct(p,q,p,r)<0) {estCote = false;break;} //ici sans le break le temps de calcul devient horrible
                if (estCote) {enveloppe.add(p); enveloppe.add(q);}
            }
        }

        return enveloppe; //ici l'enveloppe n'est pas trie dans le sens trigonometrique, et contient des doublons, mais tant pis!
    }
}
