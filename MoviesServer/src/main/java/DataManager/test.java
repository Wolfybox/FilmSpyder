package DataManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;





public class test {
	public static void main(String[] args) throws Exception {
		SessionFactory sf=new Configuration().configure().buildSessionFactory();
		Session session=sf.openSession();
		Query query=session.createQuery("from Movie m where m.moviename=?");
		query.setParameter(0, "命运速递");
		Movie m=(Movie) query.uniqueResult();
		System.out.println(m.getClass_()+"\t"+m.getMoviename());
		Set<ActorList> c=m.getActorLists();
		Iterator<ActorList> it=c.iterator();
		ActorList t;
		while(it.hasNext()) {
			t=it.next();
			System.out.println(t.getActor().getActorname());
		}
		// recommender configuration
//	    Configuration conf = new Configuration();
//	    conf.set("dfs.data.dir","/Users/lijiahui/Downloads/librec-librec-src-v2.0/data");
//	 
//	    // build data model
//	    DataModel dataModel = new TextDataModel(conf);
//	    dataModel.buildDataModel();
//	    
//	    // set recommendation context
//	    RecommenderContext context = new RecommenderContext(conf, dataModel);
//	    conf.set("rec.recommender.similarity.key" ,"item");
//	    RecommenderSimilarity similarity = new PCCSimilarity();
//	    similarity.buildSimilarityMatrix(dataModel);
//	    context.setSimilarity(similarity);
//	    conf.set("rec.neighbors.knn.number", "5");
//	    // training
//	    Recommender recommender = new ItemKNNRecommender();
//	    recommender.setContext(context);
//	    recommender.recommend(context);
//	 
//	    // evaluation
//	    RecommenderEvaluator evaluator = new RMSEEvaluator();
//        System.out.println("RMSE:" + recommender.evaluate(evaluator));
//	 
//	    // recommendation results
//	    List recommendedItemList = recommender.getRecommendedList();
//	    RecommendedFilter filter = new GenericRecommendedFilter();
//	    recommendedItemList = filter.filter(recommendedItemList);
	
	}
}
