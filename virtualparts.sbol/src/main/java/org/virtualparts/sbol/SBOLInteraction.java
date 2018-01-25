package org.virtualparts.sbol;

import static uk.ac.ncl.intbio.core.datatree.Datatree.NamespaceBinding;

import java.net.URI;

import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceOntology;
import org.sbolstandard.core2.SystemsBiologyOntology;
import org.virtualparts.sbol.Terms.sbol2;
import org.virtualparts.sbol.Terms.vpr;
import org.virtualparts.sbol.Terms.sbol2.participant;

import uk.ac.ncl.intbio.core.datatree.NamespaceBinding;

public class SBOLInteraction {
	
	public static void setName(Identified identified, ComponentDefinition active, String relationship, ComponentDefinition passive)
	{
		identified.setName(String.format("%s %s %s", getDisplayName(active), relationship, getDisplayName(passive)));		
	}
	
	public static String getName(ComponentDefinition active, String relationship, ComponentDefinition passive)
	{
		return String.format("%s %s %s", getDisplayName(active), relationship, getDisplayName(passive));		
	}
	public static class phosphorylates
	{
		public static String relation="phosphorylates";
		public static String relationLink="_" + relation + "_";		
	}
	public static class autodephoshorylation
	{
		public static String relation="auto-dephosphorylation";
		public static String relationLink="_autodephosphorylation";		
	}
	
	public static class activates
	{
		public static String relation="activates";
		public static String relationLink="_" + relation + "_";		
	}
	
	public static class represses
	{
		public static String relation="represses";
		public static String relationLink="_" + relation + "_";		
	}
	
	public static class bindsTo
	{
		public static String relation="binds to";
		public static String relationLink="_" + "bindsto" + "_";		
	}	
	
	public static class dimerisation
	{
		public static String relation="dimerises to form";
		public static String relationLink="_" + "dimerisation";		
	}
	
	public static class encodes
	{
		public static String relation="encodes";
		public static String relationLink="_" + relation + "_";		
	}
	
	
	private static String getDisplayName(Identified identified)
	{
		String name=identified.getDisplayId();
		if (identified.getName()!=null && !identified.getName().isEmpty())
		{
			name=identified.getName();
		}
		return name;
	}

	public static Interaction createPhosphorylationInteraction(ModuleDefinition moduleDef, ComponentDefinition donor, ComponentDefinition acceptor) throws SBOLValidationException {
    	FunctionalComponent fcDonor= createFunctionalComponent(moduleDef, donor);
    	FunctionalComponent fcAcceptor= createFunctionalComponent(moduleDef, acceptor);
    	
    	String interactionId=donor.getDisplayId() + phosphorylates.relationLink + acceptor.getDisplayId();
    	
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){    
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.PHOSPHORYLATION);	    	
	    	setName(interaction, donor, phosphorylates.relation, acceptor);
	    	interaction.createParticipation("donor_participation", fcDonor.getIdentity(),sbol2.participant.role.phosphateDonor);	    	
	    	interaction.createParticipation("acceptor_participation", fcAcceptor.getIdentity(),sbol2.participant.role.phosphateAcceptor);
    	}
    	return interaction;
	}

    
 /*   public static Interaction createPhosphorylationWithModifierInteraction(ModuleDefinition moduleDef, ComponentDefinition modifier, ComponentDefinition acceptor) throws SBOLValidationException {
    	FunctionalComponent fcDonor= createFunctionalComponent(moduleDef, modifier);
    	FunctionalComponent fcAcceptor= createFunctionalComponent(moduleDef, acceptor);
    	
    	String interactionId=modifier.getDisplayId() + phosphorylates.relationLink + acceptor.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){    		    	    	
    		interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.PHOSPHORYLATION);
	    	setName(interaction, modifier, phosphorylates.relation, acceptor);
	    	interaction.createParticipation("modifier_participation", fcDonor.getIdentity(),sbol2.participant.role.modifier);	    	
	    	interaction.createParticipation("acceptor_participation", fcAcceptor.getIdentity(),sbol2.participant.role.phosphateAcceptor);
	    }    	
    	return interaction;
	}

    
	public static Interaction createPhosphorylationInteraction(ModuleDefinition moduleDef, ComponentDefinition donor, ComponentDefinition acceptor) throws SBOLValidationException {
    	FunctionalComponent fcDonor= createFunctionalComponent(moduleDef, donor);
    	FunctionalComponent fcAcceptor= createFunctionalComponent(moduleDef, acceptor);
    	
    	String interactionId=donor.getDisplayId() + phosphorylates.relationLink + acceptor.getDisplayId();
    	
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){    
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.PHOSPHORYLATION);	    	
	    	setName(interaction, donor, phosphorylates.relation, acceptor);
	    	interaction.createParticipation("donor_participation", fcDonor.getIdentity(),sbol2.participant.role.phosphateDonor);	    	
	    	interaction.createParticipation("acceptor_participation", fcAcceptor.getIdentity(),sbol2.participant.role.phosphateAcceptor);
    	}
    	return interaction;
	}
*/
    
    public static Interaction createPhosphorylationWithModifierInteraction(ModuleDefinition moduleDef, ComponentDefinition modifier, ComponentDefinition acceptor) throws SBOLValidationException {
    	FunctionalComponent fcDonor= createFunctionalComponent(moduleDef, modifier);
    	FunctionalComponent fcAcceptor= createFunctionalComponent(moduleDef, acceptor);
    	
    	String interactionId=modifier.getDisplayId() + phosphorylates.relationLink + acceptor.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){    		    	    	
    		interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.PHOSPHORYLATION);
	    	setName(interaction, modifier, phosphorylates.relation, acceptor);
	    	interaction.createParticipation("modifier_participation", fcDonor.getIdentity(),sbol2.participant.role.modifier);	    	
	    	interaction.createParticipation("acceptor_participation", fcAcceptor.getIdentity(),sbol2.participant.role.phosphateAcceptor);
	    }    	
    	return interaction;
	}

    
    public static Interaction createAutoDephosphorylationInteraction(ModuleDefinition moduleDef, ComponentDefinition donor) throws SBOLValidationException {
    	FunctionalComponent fcDonor= createFunctionalComponent(moduleDef, donor);
    	String interactionId=donor.getDisplayId() + autodephoshorylation.relationLink;
    	
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){ 
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.DEPHOSPHORYLATION);	    	
	    	interaction.createParticipation("donor_participation", fcDonor.getIdentity(),sbol2.participant.role.phosphateDonor);
	    	interaction.setName(String.format("%s %s", getDisplayName(donor), autodephoshorylation.relation));	
    	}    	    	
    	return interaction;
	}
    
    

    public static Interaction createPromoterInduction(ModuleDefinition moduleDef,ComponentDefinition promoterCompDef, ComponentDefinition tfCompDef) throws SBOLValidationException
    {
    	return createPromoterInduction(moduleDef, promoterCompDef, tfCompDef, null);
    }
    
    
    public static Interaction createPromoterInduction(ModuleDefinition moduleDef,ComponentDefinition promoter, ComponentDefinition tf, URI tfRole) throws SBOLValidationException
    {
    	FunctionalComponent fcPromoter=createFunctionalComponent(moduleDef, promoter);
    	FunctionalComponent fcTF= createFunctionalComponent(moduleDef, tf);
    	
    	String interactionId=tf.getDisplayId() + activates.relationLink + promoter.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.STIMULATION);
	    	setName(interaction, tf, activates.relation, promoter);
	    	interaction.createParticipation("promoter_participation", fcPromoter.getIdentity(),Terms.sbol2.participant.role.stimulated);	   	    	
	    	Participation tfParticipation=interaction.createParticipation("tf_participation", fcTF.getIdentity(),sbol2.participant.role.activator);
	    	if (tfRole!=null)
	    	{
	    		tfParticipation.addRole(tfRole);
	    	}
	    }   	
    	return interaction;
    }
    
    public static Interaction createPromoterRepression(ModuleDefinition moduleDef,ComponentDefinition promoter, ComponentDefinition tf) throws SBOLValidationException
    {
    	FunctionalComponent fcPromoter=createFunctionalComponent(moduleDef, promoter);
    	FunctionalComponent fcTF= createFunctionalComponent(moduleDef, tf);
    	
    	String interactionId=tf.getDisplayId() + represses.relationLink + promoter.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.INHIBITION);
	    	setName(interaction, tf, represses.relation, promoter);	    	
	    	interaction.createParticipation("promoter_participation", fcPromoter.getIdentity(),Terms.sbol2.participant.role.inhibited);
	    	interaction.createParticipation("tf_participation", fcTF.getIdentity(),sbol2.participant.role.inhibitor);
	    	
	    }   	
    	return interaction;
    }
    
    public static Interaction createDNABinding(ModuleDefinition moduleDef,ComponentDefinition dna, ComponentDefinition tf, ComponentDefinition complex) throws SBOLValidationException
    {
    	FunctionalComponent fcPromoter=createFunctionalComponent(moduleDef, dna);
    	FunctionalComponent fcTF= createFunctionalComponent(moduleDef, tf);
    	FunctionalComponent fcTFDNA= createFunctionalComponent(moduleDef, complex);
    	
    	
    	String interactionId=tf.getDisplayId() + bindsTo.relationLink+ dna.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.NON_COVALENT_BINDING);	    	
	    	setName(interaction, tf, bindsTo.relation, dna);	    		    	
	    	interaction.createParticipation("dna_participation", fcPromoter.getIdentity(),sbol2.participant.role.reactant);	    		    	
	    	interaction.createParticipation("tf_participation", fcTF.getIdentity(),sbol2.participant.role.reactant);
	    	interaction.createParticipation("tf_dna_participation", fcTFDNA.getIdentity(),sbol2.participant.role.product);	    	
	    }   	
    	return interaction;
    }
    
    public static Interaction createDimerisation(ModuleDefinition moduleDef,ComponentDefinition reactant, ComponentDefinition dimer) throws SBOLValidationException
    {
    	FunctionalComponent fcReactant=createFunctionalComponent(moduleDef, reactant);
    	FunctionalComponent fcDimer= createFunctionalComponent(moduleDef, dimer);
    	
    	String interactionId=fcReactant.getDisplayId() + dimerisation.relationLink;
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.NON_COVALENT_BINDING);	  
	    	setName(interaction, reactant, dimerisation.relation, dimer);	
	    	interaction.createParticipation("reactant_participation", fcReactant.getIdentity(),sbol2.participant.role.reactant);	    	
	    	Participation dimerPar=interaction.createParticipation("dimer_participation", fcDimer.getIdentity(),sbol2.participant.role.product);
	    	//TODO Anotate dimerPar with sbPax term
	    }   	
    	return interaction;
    }
    
   /* private static URI getParticipationRole(ComponentDefinition compDef)
    {
    	if (compDef.containsType(ComponentDefinition.SMALL_MOLECULE))
    	{
    		return sbol2.participant.role.ligand;
    	}
    	else if (compDef.containsType(ComponentDefinition.COMPLEX))
    	{
    		return sbol2.participant.role.nonCovalentComplex;
    	}
    	else
    	{
    		return sbol2.participant.role.substrate;
    	}    	
    }
    */
    
    public static Interaction createComplexFormation(ModuleDefinition moduleDef,ComponentDefinition firstMolecule, ComponentDefinition secondMolecule,ComponentDefinition  complex) throws SBOLValidationException
    {
    	FunctionalComponent fcFirst=createFunctionalComponent(moduleDef, firstMolecule);
    	FunctionalComponent fcSecond= null;
    	if (secondMolecule!=null)
    	{
    		fcSecond=createFunctionalComponent(moduleDef, secondMolecule);    		
    	}
    	FunctionalComponent fcComplex= createFunctionalComponent(moduleDef, complex);
    	
    	
    	String interactionId=fcFirst.getDisplayId() + bindsTo.relationLink + secondMolecule.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.NON_COVALENT_BINDING);	  
	    	setName(interaction, firstMolecule, bindsTo.relation, secondMolecule);	
	    	Participation p1=interaction.createParticipation(firstMolecule.getDisplayId() + "_participation", fcFirst.getIdentity(),sbol2.participant.role.reactant);	   
	    	p1.addRole(vpr.species1);
	    	if (secondMolecule!=null)
	    	{
	    		Participation p2=interaction.createParticipation(secondMolecule.getDisplayId() + "_participation", fcSecond.getIdentity(),sbol2.participant.role.reactant);
	    		p2.addRole(vpr.species2);
	    	}
	    	interaction.createParticipation("complex_participation", fcComplex.getIdentity(),sbol2.participant.role.product);	    	
	    }   	
    	return interaction;
    }
    
    /*public static Interaction createDisassociation(ModuleDefinition moduleDef,ComponentDefinition firstMolecule, ComponentDefinition secondMolecule,ComponentDefinition  complex) throws SBOLValidationException
    {
    	FunctionalComponent fcsmallMolecule=createFunctionalComponent(moduleDef, firstMolecule);
    	FunctionalComponent fcProtein= createFunctionalComponent(moduleDef, secondMolecule);
    	FunctionalComponent fcComplex= createFunctionalComponent(moduleDef, complex);
    	
    	
    	String interactionId=fcComplex.getDisplayId() + "_disassociates";
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.DISSOCIATION);	  
	    	interaction.setName(fcComplex.getDisplayId() + "_disassociates");
	    	setName(interaction, firstMolecule, bindsTo.relation, secondMolecule);	
	    	Participation p1=interaction.createParticipation(firstMolecule.getDisplayId() + "_participation", fcsmallMolecule.getIdentity(),sbol2.participant.role.product);	   
	    	p1.addRole(vpr.species1);
	    	Participation p2=interaction.createParticipation(secondMolecule.getDisplayId() + "_participation", fcProtein.getIdentity(),sbol2.participant.role.product);
	    	p2.addRole(vpr.species2);
	    	interaction.createParticipation("complex_participation", fcComplex.getIdentity(),sbol2.participant.role.reactant);	    	
	    }   	
    	return interaction;
    }*/
    
    
    public static Interaction createComplexFormationWithModifier(ModuleDefinition moduleDef,ComponentDefinition reactant, ComponentDefinition modifier,ComponentDefinition  complex) throws SBOLValidationException
    {
    	FunctionalComponent fcsmallMolecule=createFunctionalComponent(moduleDef, modifier);
    	FunctionalComponent fcProtein= createFunctionalComponent(moduleDef, reactant);
    	FunctionalComponent fcComplex= createFunctionalComponent(moduleDef, complex);
    	
    	
    	String interactionId=fcsmallMolecule.getDisplayId() + bindsTo.relationLink + reactant.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.NON_COVALENT_BINDING);	  
	    	setName(interaction, modifier, bindsTo.relation, reactant);	
	    	Participation p1=interaction.createParticipation(modifier.getDisplayId() + "_participation", fcsmallMolecule.getIdentity(),sbol2.participant.role.modifier);	   
	    	p1.addRole(vpr.species1);
	    	Participation p2=interaction.createParticipation(reactant.getDisplayId() + "_participation", fcProtein.getIdentity(),sbol2.participant.role.reactant);
	    	p2.addRole(vpr.species2);
	    	interaction.createParticipation("complex_participation", fcComplex.getIdentity(),sbol2.participant.role.product);	    	
	    }   	
    	return interaction;
    }
    
    public static Interaction createComplexFormationWithReactantModifier(ModuleDefinition moduleDef,ComponentDefinition reactant, ComponentDefinition modifier,ComponentDefinition  complex) throws SBOLValidationException
    {
    	FunctionalComponent fcsmallMolecule=createFunctionalComponent(moduleDef, modifier);
    	FunctionalComponent fcProtein= createFunctionalComponent(moduleDef, reactant);
    	FunctionalComponent fcComplex= createFunctionalComponent(moduleDef, complex);
    	
    	
    	String interactionId=fcsmallMolecule.getDisplayId() + bindsTo.relationLink + reactant.getDisplayId();
    	Interaction interaction= moduleDef.getInteraction(interactionId);
    	if (interaction==null){
	    	interaction=moduleDef.createInteraction(interactionId, SystemsBiologyOntology.NON_COVALENT_BINDING);	  
	    	setName(interaction, modifier, bindsTo.relation, reactant);	
	    	Participation p1=interaction.createParticipation(modifier.getDisplayId() + "_participation", fcsmallMolecule.getIdentity(),sbol2.participant.role.reactant);	   
	    	p1.addRole(vpr.species1);
	    	Participation p2=interaction.createParticipation(reactant.getDisplayId() + "_participation", fcProtein.getIdentity(),sbol2.participant.role.reactant);
	    	p2.addRole(vpr.species2);
	    	interaction.createParticipation("complex_participation", fcComplex.getIdentity(),sbol2.participant.role.product);	
	    	interaction.createParticipation(modifier.getDisplayId() + "_productparticipation", fcsmallMolecule.getIdentity(),sbol2.participant.role.product);	
	    	
	    }   	
    	return interaction;
    }
    
    private static FunctionalComponent createFunctionalComponent(ModuleDefinition moduleDef,ComponentDefinition compDef) throws SBOLValidationException
    {
    	FunctionalComponent fc=moduleDef.getFunctionalComponent(compDef.getDisplayId());
    	if (fc==null)
    	{
    			fc=moduleDef.createFunctionalComponent(compDef.getDisplayId(), AccessType.PUBLIC, compDef.getIdentity(), DirectionType.INOUT);
    			if (compDef.containsType(ComponentDefinition.SMALL_MOLECULE))
    			{
    				fc.setDirection(DirectionType.IN);
    			}
    	}
    	return fc;
    }
    
    public static Interaction createTranslationInteraction(ModuleDefinition moduleDef,ComponentDefinition cds, ComponentDefinition protein) throws SBOLValidationException
    {
    	FunctionalComponent fcCds=createFunctionalComponent(moduleDef, cds);
	    FunctionalComponent fcProtein=createFunctionalComponent(moduleDef, protein);
	    
	    String interactionId=cds.getDisplayId() + encodes.relationLink+ protein.getDisplayId();
	    Interaction interaction=moduleDef.getInteraction(interactionId);
	    if (interaction==null)
	    {
	    	interaction=moduleDef.createInteraction(interactionId,  SystemsBiologyOntology.GENETIC_PRODUCTION);
	    	setName(interaction, cds, encodes.relation, protein);
	    	interaction.createParticipation("cds_participation", fcCds.getIdentity(),participant.role.template);	    	    	
	        interaction.createParticipation("product_participation", fcProtein.getIdentity(),SystemsBiologyOntology.PRODUCT);
	    }	    	
	    return interaction;
    }
    
    
    public static Interaction createDegradationInteraction(ModuleDefinition moduleDef,ComponentDefinition comp) throws SBOLValidationException
    {
    	FunctionalComponent fc=createFunctionalComponent(moduleDef, comp);
	    
	    String interactionId=comp.getDisplayId() + "_degradation";
	    Interaction interaction=moduleDef.getInteraction(interactionId);
	    if (interaction==null)
	    {
	    	interaction=moduleDef.createInteraction(interactionId,  SystemsBiologyOntology.DEGRADATION);
	    	interaction.setName(comp.getDisplayId() + " degradation");
	    	interaction.createParticipation("comp_participation", fc.getIdentity(),participant.role.reactant);	    	    	
	    }	    	
	    return interaction;
    }
}
