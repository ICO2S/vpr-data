package org.virtualparts.sbol;

import static uk.ac.ncl.intbio.core.datatree.Datatree.NamespaceBinding;

import java.net.URI;

import javax.xml.namespace.QName;

import org.sbolstandard.core2.SystemsBiologyOntology;

import uk.ac.ncl.intbio.core.datatree.NamespaceBinding;

public class Terms {

	public static final NamespaceBinding molecularInteractions = NamespaceBinding("http://identifiers.org/psimi/", "psimi");		
	
	public static final class ncbi {
		public static final NamespaceBinding Ns = NamespaceBinding("http://www.ncbi.nlm.nih.gov#", "ncbi");

		// http://www.uniprot.org/help/taxonomic_identifier
		//public static final QName taxonId = Ns.withLocalPart("taxid");
		public static final QName taxonId = edam.Ns.withLocalPart("data_1179");
		
		// http://www.biomedcentral.com/1471-2105/11/319
		public static final QName taxonIncrementNumber = Ns.withLocalPart("taxonIncrementNumber");
	}

	public static final class taxonomy {
		public static final NamespaceBinding Ns = NamespaceBinding("http://identifiers.org/taxonomy/", "taxonomy");
		public static final QName bacillusSubtilis168 = Ns.withLocalPart("224308");
		public static final QName ecoli = Ns.withLocalPart("562");
		public static final QName bacillusSubtilis = Ns.withLocalPart("1423");
		
		
	}

	public static URI toURI(QName qname) {
		return URI.create(qname.getNamespaceURI() + qname.getLocalPart());
	}

	public static final class sbo {
		public static final NamespaceBinding Ns = NamespaceBinding("http://identifiers.org/biomodels.sbo/", "sbo");
	}
	

	public static final class mirnatarget {
		public static final NamespaceBinding Ns = NamespaceBinding("http://purl.obolibrary.org/obo/", "obo");
	}
	
	public static final class sbol2 {
		public static final NamespaceBinding Ns = NamespaceBinding("http://sbols.org/v2#", "sbol2");
		
		public static final class vocabulary
		{
		public static final QName ComponentDefinition  = sbol2.Ns.withLocalPart("ComponentDefinition");
	        public static final QName ModuleDefinition  = sbol2.Ns.withLocalPart("ModuleDefinition");
	        public static final QName Participation  = sbol2.Ns.withLocalPart("Participation");
	        public static final QName FunctionalComponent= sbol2.Ns.withLocalPart("FunctionalComponent");
	        public static final QName Module= sbol2.Ns.withLocalPart("Module");
	        public static final QName Model= sbol2.Ns.withLocalPart("Model");
	        public static final QName type  = sbol2.Ns.withLocalPart("type");
	        public static final QName Sequence  = sbol2.Ns.withLocalPart("Sequence");
	        public static final QName sequence= sbol2.Ns.withLocalPart("sequence");
	        public static final QName role= sbol2.Ns.withLocalPart("role");     
	        public static final QName elements= sbol2.Ns.withLocalPart("elements");     
	        public static final QName encoding= sbol2.Ns.withLocalPart("encoding");      
	        public static final QName displayId= sbol2.Ns.withLocalPart("displayId");  
	        public static final QName version= sbol2.Ns.withLocalPart("version");  	        
	        public static final QName persistentIdentity= sbol2.Ns.withLocalPart("persistentIdentity");  
	        public static final QName interaction= sbol2.Ns.withLocalPart("interaction");  
	        public static final QName participation= sbol2.Ns.withLocalPart("participation");
	        public static final QName participant= sbol2.Ns.withLocalPart("participant");
	        public static final QName module= sbol2.Ns.withLocalPart("module");
	        public static final QName functionalComponent= sbol2.Ns.withLocalPart("functionalComponent");
	        public static final QName model= sbol2.Ns.withLocalPart("model");
	        public static final QName definition= sbol2.Ns.withLocalPart("definition");       
	        public static final QName access= sbol2.Ns.withLocalPart("access");       
	        public static final QName direction= sbol2.Ns.withLocalPart("direction");       
	        public static final QName mapsTo= sbol2.Ns.withLocalPart("mapsTo");    
		}
		
		public static final class componentDefinition
		{
			public static final class type {
				public static final QName dna = biopax.dna;
				public static final QName smallMolecule = biopax.smallMolecule;
				public static final QName protein = biopax.protein;					
			}
		}
		
		public static final class interaction{
			public static final class type {
				//public static URI dimerisation =  toURI(mirnatarget.Ns.withLocalPart("OMIT_0019352"));	
				public static URI dimerisation =  toURI(mirnatarget.Ns.withLocalPart("OMIT_0019352"));					
				public static URI geneticProduction= SystemsBiologyOntology.GENETIC_PRODUCTION;		
				public static URI inhibition= SystemsBiologyOntology.INHIBITION;	
				public static URI activation= SystemsBiologyOntology.STIMULATION;									
			}
		}
		public static final class participant {
			public static final class role {
				public static URI product = SystemsBiologyOntology.PRODUCT;//toURI(sbo.Ns.withLocalPart("SBO:0000011"));
				public static URI substrate = toURI(sbo.Ns.withLocalPart("SBO:0000015"));
				//public static URI modifier = toURI(sbo.Ns.withLocalPart("SBO:0000019"));
				public static URI modifier = SystemsBiologyOntology.MODIFIER;
				public static URI template = toURI(sbo.Ns.withLocalPart("SBO:0000645"));				
				public static URI phosphateAcceptor = toURI(molecularInteractions.withLocalPart("MI:0843"));
				//public static URI phosphateAcceptorPhosphorylated = toURI(molecularInteractions.withLocalPart("MI:0843#phosphorylated"));		
				public static URI phosphateDonor= toURI(molecularInteractions.withLocalPart("MI:0842"));
				public static URI activator = SystemsBiologyOntology.STIMULATOR;
				public static URI inhibitor = SystemsBiologyOntology.INHIBITOR;////public static QName inhibitor = sbo.Ns.withLocalPart("SBO:0000020");
				public static URI reactant=SystemsBiologyOntology.REACTANT;
				//GM: public static URI dimer=toURI(sbo.Ns.withLocalPart("SBO:0000607"));
				public static URI inhibited= toURI(sbo.Ns.withLocalPart("SBO:0000642"));
				public static URI stimulated= toURI(sbo.Ns.withLocalPart("SBO:0000643"));				
				//GM public static URI proteinComplex1= toURI(sbo.Ns.withLocalPart("SBO:0000297"));
				//GM public static URI ligand= toURI(sbo.Ns.withLocalPart("SBO:0000280"));
				//GM: public static URI nonCovalentComplex= toURI(sbo.Ns.withLocalPart("SBO:0000253"));
				
				
				
				
				
				
				
				
				
				//public static URI phosphateDonorPhosphorylated = toURI(molecularInteractions.withLocalPart("MI:0842#phosphorylated"));

			}
		}

		public static final class model {
			public static final class language {
				public static URI sbml = URI.create("http://identifiers.org/edam/format_2585");
			}

			public static final class framework {
				public static URI continuous = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000062");
				public static URI discrete = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000063");
				public static URI fluxbalance = URI.create("http://identifiers.org/biomodels.sbo/SBO:0000624");
			}

			public static final class role {
				public static URI part = URI.create("http://virtualparts.org/ont#partmodel");
				public static URI interaction = URI.create("http://virtualparts.org/ont#interactionmodel");
			}
		}

		public static final class documented {
			public static final QName name = dcterms.title;
			public static final QName description = dcterms.description;
		}
		
	}

	public static final class dcterms {
		public static final NamespaceBinding Ns = NamespaceBinding("http://purl.org/dc/terms/", "dcterms");
		public static final QName title = Ns.withLocalPart("title");
		public static final QName description = Ns.withLocalPart("description");
	}

	public static final class so {
		public static final NamespaceBinding Ns = NamespaceBinding("http://purl.org/obo/owl/SO#", "so");
		public static final NamespaceBinding NsSoIdentifiers = NamespaceBinding("http://identifiers.org/so/","soidentifiers");
	}

	public static final class biopax {
		public static final NamespaceBinding Ns = NamespaceBinding("http://www.biopax.org/release/biopax-level3.owl#", "biopax");
		public static final QName smallMolecule = Ns.withLocalPart("SmallMolecule");
		public static final QName dna = Ns.withLocalPart("DnaRegion");
		public static final QName protein = Ns.withLocalPart("Protein");
		
	}
	
	
	public static final class rdfs {
		public static final NamespaceBinding Ns = NamespaceBinding("http://www.w3.org/2000/01/rdf-schema#", "rdfs");
		
	}

	public static final class rdf {
		public static final NamespaceBinding Ns = NamespaceBinding("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf");
		
	}
	
	public static final class edam {
		public static final NamespaceBinding Ns = NamespaceBinding("http://identifiers.org/edam/", "edam");
		public static final QName searchParameter = Ns.withLocalPart("data_2079");
		public static final QName ncbiTaxonId = Ns.withLocalPart("data_1179");
	}
	
	public static final class vpr {
		public static final NamespaceBinding Ns = NamespaceBinding("http://www.virtualparts.org/terms#", "vpr");
		public static URI species1 = toURI(Ns.withLocalPart("species1"));
		public static URI species2 = toURI(Ns.withLocalPart("species2"));
		
	}
	
	
}



/*public static final class signal
{
	public static URI mrna = toURI(Ns.withLocalPart("mrna"));		
	public static URI pops = toURI(Ns.withLocalPart("pops"));
	public static URI rips = toURI(Ns.withLocalPart("rips"));
	public static URI volume = toURI(Ns.withLocalPart("volume"));
	public static URI component = toURI(Ns.withLocalPart("component"));
	public static URI translationRate = toURI(Ns.withLocalPart("translationRate"));
	public static URI cdsproduct = SystemsBiologyOntology.PRODUCT;
	public static URI species1 = toURI(Ns.withLocalPart("species1"));
	public static URI species2 = toURI(Ns.withLocalPart("species2"));
	public static URI kforward = toURI(Ns.withLocalPart("kforward"));
	public static URI kback = toURI(Ns.withLocalPart("kback"));
	public static URI kphosphorylation = toURI(Ns.withLocalPart("kp"));		
	public static URI kdephosphorylation = toURI(Ns.withLocalPart("kdep"));				
	public static URI complex = toURI(Ns.withLocalPart("complex"));
	public static URI activator = SystemsBiologyOntology.STIMULATOR;
	public static URI inhibitor = SystemsBiologyOntology.INHIBITOR;		
	public static URI phosphateAcceptor = toURI(molecularInteractions.withLocalPart("MI:0843"));
	public static URI phosphateAcceptorPhosphorylated = toURI(molecularInteractions.withLocalPart("MI:0843#phosphorylated"));		
	public static URI phosphateDonor= toURI(molecularInteractions.withLocalPart("MI:0842"));
	public static URI phosphateDonorPhosphorylated = toURI(molecularInteractions.withLocalPart("MI:0842#phosphorylated"));
	public static URI modifier= SystemsBiologyOntology.MODIFIER;
	public static URI phosphorylated= toURI(phenotypicQuality.withLocalPart("PATO:0002220"));
	public static URI kdeg=toURI(Ns.withLocalPart("kdeg"));
	public static URI sigmaFactor=toURI(Ns.withLocalPart("sigmaFactor"));		
	public static URI reactant=SystemsBiologyOntology.REACTANT;
	public static URI dimer=toURI(sbo.withLocalPart("SBO:0000607"));
	public static URI sigmaA=toURI(Ns.withLocalPart("SigmaA"));
	public static URI pfreeCisEntities=toURI(Ns.withLocalPart("ProbabilityOfFreeCisEntities"));
	public static URI copyNumber = toURI(Ns.withLocalPart("copyNumber"));
	
}*/
