package eu.learnpad.ca.analysis.simplicity;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.languagetool.Language;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.language.BritishEnglish;
import org.languagetool.language.Italian;

import eu.learnpad.ca.analysis.simplicity.difficultjargon.AlternativeTerm;
import eu.learnpad.ca.analysis.simplicity.difficultjargon.AlternativeTermSet;
import eu.learnpad.ca.rest.data.Annotation;
import eu.learnpad.ca.rest.data.Node;

import gate.DocumentContent;
import gate.util.InvalidOffsetException;


public class DifficultJargonAlternative { 


	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DifficultJargonAlternative.class);

	private AlternativeTermSet alternativetermset;
	private Language language;
	private DocumentContent docContent;



	public DifficultJargonAlternative(Language lang, DocumentContent docContent) {
		this.language=lang;
		alternativetermset  = readAlternativeTerms(lang);
		this.docContent=docContent;
	}




	public List<Annotation> checkUnclearAcronym(Set<gate.Annotation> listsentence, Set<gate.Annotation> listSentenceDefected) {
		List<Annotation> annotations =new ArrayList<Annotation>();
		int id = 900_000;
		for (gate.Annotation sentence_gate : listsentence) {


			gate.Node gatenodestart = sentence_gate.getStartNode();
			gate.Node gatenodeend =  sentence_gate.getEndNode();
			try{

				DocumentContent sentence = docContent.getContent(gatenodestart.getOffset(),gatenodeend.getOffset());

				int len = annotations.size();
				id= insertdefectannotationsentence(sentence.toString(), id, annotations,gatenodestart.getOffset().intValue());
				if(annotations.size()>len){
				  if(!listSentenceDefected.contains(sentence_gate))
					  listSentenceDefected.add(sentence_gate);
				}

			}catch(InvalidOffsetException e){
				log.debug(e);
			}

		}
		return annotations;

	}



	private AlternativeTermSet readAlternativeTerms(Language lang){
		InputStream is = null;
		if(lang instanceof BritishEnglish | lang instanceof AmericanEnglish){
			is = DifficultJargonAlternative.class.getClassLoader().getResourceAsStream("AlternativeTermSet_EnglishLatin.xml");

		}else
			if(lang instanceof Italian){
				is = DifficultJargonAlternative.class.getClassLoader().getResourceAsStream("AlternativeTermSet_EnglishLatin.xml");

			}

		assert is!=null;

		try {
			JAXBContext jaxbContexti = JAXBContext.newInstance(AlternativeTermSet.class);


			Unmarshaller jaxbUnmarshaller1 = jaxbContexti.createUnmarshaller();
			AlternativeTermSet atSet = (AlternativeTermSet) jaxbUnmarshaller1.unmarshal(is);
			return atSet;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}


	}





	private int insertdefectannotationsentence(String sentence,
			int nodeid, List<Annotation> annotations, int offset) {
		List<AlternativeTerm> listAltTermSet = alternativetermset.getAlternativeterms();

		String [] spliter = sentence.split("[\\W]");
		Map<String, Integer> elementfinded = new HashMap<String, Integer>();
		int precedentposition=0;

		for (int i = 0; i < spliter.length; i++) {

			String token = spliter[i];
			AlternativeTerm tmptoken = new AlternativeTerm(token);
			if(listAltTermSet.contains(tmptoken)){
				int initialpos = indexofElement(sentence,token,elementfinded,"[\\W]");
				int finalpos = initialpos+token.length();
				if(precedentposition>initialpos){
					//initialpos = sentence.lastIndexOf(token);
					//finalpos = initialpos+token.length();
					System.out.println();
					log.error("token not find");
				}
				//String stringap = sentence.substring(precedentposition, initialpos);
				
				precedentposition=finalpos;
				nodeid++;
				Node init= new Node(nodeid,initialpos+offset);
				nodeid++;
				Node end= new Node(nodeid,finalpos+offset);
				

				Annotation a = new Annotation();
				a.setId(nodeid);
				a.setEndNode(end.getId());
				a.setStartNode(init.getId());
				a.setNodeEnd(end);
				a.setNodeStart(init);
				a.setType("DifficultJargon Alternative");

				String suggestion = listAltTermSet.get(listAltTermSet.indexOf(tmptoken)).getSuggestion();
				a.setRecommendation("The term "+tmptoken.getWord()+" is difficult. Please replace with: "+suggestion);
				annotations.add(a);

			}
		}
		/*if(precedentposition<sentence.length()){
			String stringap = sentence.substring(precedentposition, sentence.length());
			//c.setContent(stringap);
		}
		if(annotations.size()==0){
			c.setContent(sentence);
		}*/

		return nodeid;

	}

	protected  int indexofElement(String sentence, String word, Map<String, Integer> elementfinded, String split){
		String [] spliter = sentence.split(split);
		int position = 0;
		int numwordfinded = 0;
		for (int i = 0; i < spliter.length; i++) {
			int offset = 0;
			String token = spliter[i];
			if(token.equals(word)){
				numwordfinded++;
				if(!elementfinded.containsKey(token)){
					elementfinded.put(token, 1);
				}
				if(elementfinded.get(token).intValue()==numwordfinded){
					Integer I = elementfinded.get(token);
					int y  = I.intValue()+1;
					elementfinded.put(token, y);
					return position;
				}else{
					position+=token.length()+1+offset;
				}
			}else{
				position+=token.length()+1+offset;
			}
		}
		return position;
	}

}