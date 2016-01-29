package cz.cuni.amis.pogamut.sposh.elements;

import cz.cuni.amis.pogamut.sposh.exceptions.CycleException;
import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Root node of whole lap plan, contains {@link DriveCollection}, all {@link Competence}
 * and {@link ActionPattern} + optional documentation.
 *
 * @see DriveCollection top level of the decision tree
 * @see ActionPattern
 * @see Competence
 * @see TriggeredAction
 * @author HonzaH
 */
public final class PoshPlan extends PoshDummyElement<PoshPlan, PoshPlan> {

    /**
     * Name of the plan, from docnode, optional.
     */
    private String name = "";
    /**
     * Author of the plan, optional.
     */
    private String author = "";
    /**
     * Information about plan, optional.
     */
    private String info = "";
    /**
     * Drive collection of the plan. The top level decision choices.
     */
    private final DriveCollection dc;
    /**
     * List of all action patterns in the plan.
     */
    private final List<ActionPattern> aps = new ArrayList<ActionPattern>();
    private final List<ActionPattern> apsUm = Collections.unmodifiableList(aps);
    /**
     * List of all competences in the plan.
     */
    private final List<Competence> cs = new ArrayList<Competence>();
    private final List<Competence> csUm = Collections.unmodifiableList(cs);    
    /**
     * List of all adopts in the plan.
     */
    private final List<Adopt> ads = new ArrayList<Adopt>();
    private final List<Adopt> adsUm = Collections.unmodifiableList(ads);    
    /**
     * Data flavor of posh plan(for drag-and-drop), not used anywhere, but
     * required by interface.
     */
    public static final DataFlavor dataFlavor = new DataFlavor(PoshPlan.class, "posh_tree_root");
    /**
     * Name of the property for {@link #name}.
     */
    public static final String PROP_NAME = "posh-plan-name";
    /**
     * Name of the property for {@link #author}.
     */
    public static final String PROP_AUTHOR = "posh-plan-author";
    /**
     * Name of the property for {@link #info}.
     */
    public static final String PROP_INFO = "posh-plan-info";

    /**
     * Create new plan along with empty {@link DriveCollection} as its main 
     * decision point.
     * @param driveCollectionName Name of the {@link DriveCollection} of 
     * the plan.
     */
    PoshPlan(String driveCollectionName) {
        this.dc = LapElementsFactory.createDriveCollection(driveCollectionName);
        this.dc.setParent(this);
    }

    
    private Set<String> getReachableActionNames() {
        Set<String> reachableNames = new HashSet<String>();
        
        for (DriveElement drive : dc.getDrives()) {
            String driveActionName = drive.getAction().getName();
            if (isAP(driveActionName)) {
                ActionPattern actionPattern = getAP(driveActionName);
                reachableNames.addAll(getActionPatternActionNames(actionPattern));
                
            } else if (isC(driveActionName)) {
                Competence competence = getC(driveActionName);
                reachableNames.addAll(getCompetenceActionNames(competence));
            } else {
                reachableNames.add(driveActionName);
            }
        }
        
        return reachableNames;
    }
    
    private Set<String> getCompetenceActionNames(Competence c) {
        throw new UnsupportedOperationException();
    }

    private Set<String> getActionPatternActionNames(ActionPattern ap) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get all action names in the plan, i.e. actions is DC, AP and C. Careful,
     * if name of action is same as name of AP/C, it is expanded and thus is not
     * a name of action, but only reference to the AP/C.
     *
     * @return Names of all actions in the plan.
     */
    public Set<String> getActionsNames() {
        Set<String> actionNames = new HashSet<String>();
        List<TriggeredAction> actions = getActions();
        
        for (TriggeredAction action : actions) {
            actionNames.add(action.getName());
        }
        
        return actionNames;
    }
    
    /**
     * Get all actions in this plan. It includes all actions in drives, 
     * competence elements, action patterns and adopts.
     * 
     * NOTE: This adds all actions
     * @return Set of all actions in the plan.
     */
    public List<TriggeredAction> getActions() {
        List<TriggeredAction> references = getAllReferences();
        List<TriggeredAction> actions = new LinkedList<TriggeredAction>();
        
        for (TriggeredAction reference : references) {
            String referenceName = reference.getName();
            if (!isAP(referenceName) && !isC(referenceName) && !isAD(referenceName)) {
                actions.add(reference);
            }
        }
        return actions;
    }

    public List<TriggeredAction> getAllReferences() {
        List<TriggeredAction> references = new LinkedList<TriggeredAction>();
        for (DriveElement drive : dc.getDrives()) {
            references.add(drive.getAction());
        }
        for (Competence competence : csUm) {
            for (CompetenceElement competenceElement : competence.getChildDataNodes()) {
                references.add(competenceElement.getAction());
            }
        }
        for (ActionPattern actionPattern : apsUm) {
            for (TriggeredAction action : actionPattern.getActions()) {
                references.add(action);
            }
        }
        for (Adopt adopt : adsUm) {
        	references.add(adopt.getAdoptedElement());
        }
        return references;
    }
    
    /**
     * Get all sense names in the plan, i.e. senses in DC goal, DE trigger, CE
     * trigger.
     *
     * @return Names of all senses in all elements of the plan.
     */
    public Set<String> getSensesNames() {
        Set<String> senseNames = new HashSet<String>();

        addTriggerSenseNames(dc.getGoal(), senseNames);

        for (DriveElement drive : dc.getDrives()) {
            addTriggerSenseNames(drive.getTrigger(), senseNames);
        }
        for (Competence competence : csUm) {
            for (CompetenceElement competenceElement : competence.getChildDataNodes()) {
                addTriggerSenseNames(competenceElement.getTrigger(), senseNames);
            }
        }
        for (Adopt adopt : adsUm) {
        	addTriggerSenseNames(adopt.getExitCondition(), senseNames);
        }
        
        return senseNames;
    }

    /**
     * Add names of the senses in the trigger into passed set.
     *
     * @param trigger trigger containing the senses
     * @param senseNames set into which to add names
     */
    private void addTriggerSenseNames(Trigger<?> trigger, Set<String> senseNames) {
        for (Sense triggerSense : trigger) {
            senseNames.add(triggerSense.getName());
        }
    }

    /**
     * Does the plan contains {@link Competence} with specified name?
     *
     * @param name name we are checking
     * @return true if C with name exists in the plane, false otherwise.
     */
    public boolean isC(String name) {
        return getC(name) != null;
    }

    /**
     * Return competence from this plan with specified name.
     *
     * @param name name of searched C
     * @return competence or null if such C doesn't exists
     */
    public Competence getC(String name) {
        for (Competence c : getCompetences()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }


    /**
     * Get competence with id, equivalent of {@link #getCompetences() }.{@link List#get(int) }.
     */
    public Competence getCompetence(int id) {
        return csUm.get(id);
    }
        
    
    /**
     * Does this plan contain AP with specified name?
     *
     * @param name name of AP we are checking
     * @return true if AP with name exists in the plane, false otherwise.
     */
    public boolean isAP(String name) {
        return getAP(name) != null;
    }

    /**
     * Return action pattern from the plan with specified name.
     *
     * @param name name of searched AP
     * @return action pattern or null if such AP doesn't exists
     */
    public ActionPattern getAP(String name) {
        for (ActionPattern ap : getActionPatterns()) {
            if (ap.getName().equals(name)) {
                return ap;
            }
        }
        return null;
    }

    /**
     * Get action pattern with id, equivalent of {@link #getActionPatterns() }.{@link List#get(int) }.
     */
    public ActionPattern getActionPattern(int id) {
        return apsUm.get(id);
    }
    
    /**
     * Does this plan contain AD with specified name?
     *
     * @param name name of AD we are checking
     * @return true if AD with name exists in the plane, false otherwise.
     */
    public boolean isAD(String name) {
    	return getAD(name) != null;
    }
    
    /**
     * Returns adopt from the plan with specified name.
     *
     * @param name name of searched AD
     * @return adopt or null if such AD doesn't exists
     */
    public Adopt getAD(String name) {
        for (Adopt ad : getAdopts()) {
            if (ad.getName().equals(name)) {
                return ad;
            }
        }
        return null;
    }

    /**
     * Get adopt with id, equivalent of {@link #getAdopts() }.{@link List#get(int) }.
     */
    public Adopt getAdopt(int id) {
        return adsUm.get(id);
    }
    
    
    /**
     * Check if passed string is different than names of all referencable nodes (competences, and
     * action patterns and adopts). If it is, it can be used as name of new competence or
     * action pattern.
     *
     * @param testedName name of tested string.
     * @return true if it is unique, false otherwise.
     */
    public boolean isUniqueNodeName(String testedName) {
        return !isAP(testedName) && !isC(testedName) && !isAD(testedName);
    }

    /**
     * Add competence node to the lap tree (add, emit)
     *
     * @param competenceNode
     */
    public void addCompetence(Competence competence) throws DuplicateNameException, CycleException {
        if (!isUniqueNodeName(competence.getName())) {
            throw DuplicateNameException.create(competence.getName());
        }

        PoshPlan orgParent = competence.getParent();
        cs.add(competence);
        competence.setParent(this);

        if (isCycled()) {
            competence.setParent(orgParent);
            cs.remove(competence);

            throw CycleException.createFromName(competence.getName());
        }

        emitChildNode(competence);
    }
    
    public void addAdopt(Adopt adopt) throws DuplicateNameException, CycleException {
    	if (!isUniqueNodeName(adopt.getName())) {
            throw DuplicateNameException.create(adopt.getName());
        }

        PoshPlan orgParent = adopt.getParent();
        ads.add(adopt);
        adopt.setParent(this);

        if (isCycled()) {
        	adopt.setParent(orgParent);
        	ads.remove(adopt);

            throw CycleException.createFromName(adopt.getName());
        }

        emitChildNode(adopt);
    }

    /**
     * Get name of the plan. Name is the first string in <em>documentation</em>
     * node.
     *
     * @return Name of the plan or empty string if name is not set.
     */
    public String getName() {
        return name;
    }

    /**
     * Name of the plan, from docnode, optional.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        assert name != null;
        // XXX: check the input m#9
        String oldName = this.name;
        this.name = name;

        firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * Get author of the plan, optional. Author is the second string in the 
     * <em>documentation</em> node of the tree. 
     *
     * @return author of the plan or empty string if author is not set.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Set new author of the plan.
     *
     * @param author new author or empty string for erasing.
     */
    public void setAuthor(String author) {
        assert author != null;
        // XXX: check inputs m#9
        String oldAuthor = this.author;
        this.author = author;

        firePropertyChange(PROP_AUTHOR, oldAuthor, author);
    }

    /**
     * Get info about this plan. Description of the plan is the third string of 
     * the <em>documentation</em> node.
     *
     * @return informationa about this plan or empty string if info is not set.
     */
    public String getInfo() {
        return this.info;
    }

    /**
     * Set informations about the plan. 
     *
     * @param info New info about plan or or empty string.
     */
    public void setInfo(String info) {
        assert info != null;
        // XXX: check inputs m#9
        String oldInfo = this.info;
        this.info = info;

        firePropertyChange(PROP_INFO, oldInfo, info);
    }

    /**
     * Get list of all competences.
     *
     * @return Unmodifiable list of all competences.
     */
    public List<Competence> getCompetences() {
        return csUm;
    }

    /**
     * Add new AP to the lap plan (add, emit)
     *
     * @param actionPatternNode
     */
    public void addActionPattern(ActionPattern actionPattern) throws DuplicateNameException, CycleException {
        if (!this.isUniqueNodeName(actionPattern.getName())) {
            throw new DuplicateNameException("Action pattern '" + actionPattern.getName() + "' has duplicate name in POSH plan.");
        }

        PoshPlan orgParent = actionPattern.getParent();
        this.aps.add(actionPattern);
        actionPattern.setParent(this);

        // test cycle
        if (this.isCycled()) {
            actionPattern.setParent(orgParent);
            this.aps.remove(actionPattern);

            throw CycleException.createFromName(name);
        }

        emitChildNode(actionPattern);
    }

    /**
     * Get list of all ADs in the plan.
     *
     * @return Unmodifiable list of all ADs in the plan.
     */
    public List<Adopt> getAdopts() {
        return adsUm;
    }
    
    /**
     * Get list of all APs in the plan.
     *
     * @return Unmodifiable list of all APs in the plan.
     */
    public List<ActionPattern> getActionPatterns() {
        return apsUm;
    }

    /**
     * Get drive collection of this plan.
     */
    public DriveCollection getDriveCollection() {
        return dc;
    }

    /**
     * Is some element (AP/C) of the lap plan cycled? Doesn't even have to be
     * attached to the drive.
     *
     * @return true if plan has a cycle, false otherwise
     */
    public boolean isCycled() {

        for (ActionPattern apNode : this.aps) {
            if (findCycle(apNode, new HashSet<String>())) {
                return true;
            }
        }
        for (Competence compNode : this.cs) {
            if (findCycle(compNode, new HashSet<String>())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Try to find cycle from the AP subtree using DFS.
     * <p/>
     * The set contains actions on the the path from the root node to the parent
     * of current node (apNode). If name of apNode is found in the set, we have
     * a cycle (such name would be reference to some element along the path).
     *
     * @param apNode current node
     * @param set set of all action names found from the root node to the parent
     * of the apNode
     * @return true if we have a cycle, false if not.
     */
    private boolean findCycle(ActionPattern apNode, Set<String> set) {
        if (set.contains(apNode.getName())) {
            return true;
        }
        set.add(apNode.getName());

        for (TriggeredAction action : apNode.getActions()) {
            ActionPattern actionAP;
            if ((actionAP = getAP(action.getName())) != null) {
                if (findCycle(actionAP, set)) {
                    return true;
                }
            }

            Competence actionComp;
            if ((actionComp = getC(action.getName())) != null) {
                if (findCycle(actionComp, set)) {
                    return true;
                }
            }
        }
        set.remove(apNode.getName());
        return false;
    }

    /**
     * Try to find cycle using DFS.
     * <p/>
     * The set contains actions on the the path from the root node to the parent
     * of current node (compNode). If name of compNode is found in the set, we
     * have a cycle (such name would be reference to some element along the
     * path).
     *
     * @param compNode current node
     * @param set set of all action names found from the root node to the parent
     * of the compNode
     * @return true if we have a cycle, false if not.
     */
    private boolean findCycle(Competence compNode, Set<String> set) {
        if (set.contains(compNode.getName())) {
            return true;
        }

        set.add(compNode.getName());

        for (CompetenceElement element : compNode.getChildDataNodes()) {
            TriggeredAction action = element.getAction();

            ActionPattern actionAP;
            if ((actionAP = getAP(action.getName())) != null) {
                if (findCycle(actionAP, set)) {
                    return true;
                }
            }

            Competence actionComp;
            if ((actionComp = getC(action.getName())) != null) {
                if (findCycle(actionComp, set)) {
                    return true;
                }
            }
        }
        set.remove(compNode.getName());
        return false;
    }

    /**
     * Return serializaton of lap tree.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");

        if (!name.isEmpty() || !author.isEmpty() || !info.isEmpty()) {
            sb.append("\n\t(documentation \"");
            sb.append(name.replace("\"", ""));
            sb.append("\" \"");
            sb.append(author.replace("\"", ""));
            sb.append("\" \"");
            sb.append(info.replace("\"", ""));
            sb.append("\")\n");
        }
        for (Competence node : cs) {
            sb.append('\n');
            sb.append(node.toString());
        }
        for (ActionPattern node : this.aps) {
            sb.append('\n');
            sb.append(node.toString());
        }
        sb.append('\n');
        sb.append(dc.toString());
        sb.append("\n)");

        return sb.toString();
    }

    @Override
    public List<PoshElement> getChildDataNodes() {
        List<PoshElement> children = new ArrayList<PoshElement>();

        children.addAll(cs);
        children.addAll(aps);
        children.addAll(ads);
        children.add(dc);

        return children;
    }

    @Override
    public boolean moveChild(int newIndex, PoshElement child) {
        throw new UnsupportedOperationException();
/*        if (cs.contains(child)) {
            return moveNodeInList(cs, child, relativePosition);
        }
        if (aps.contains(child)) {
            return moveNodeInList(aps, child, relativePosition);
        }
        return false;*/
    }

    @Override
    public DataFlavor getDataFlavor() {
        return dataFlavor;
    }

    @Override
    public LapType getType() {
        return LapType.PLAN;
    }
    
    /**
     * Remove competence from the plan and notify listeners about removal of
     * child.
     *
     * @param removeCompetence Competence to be removed
     */
    public void removeCompetence(Competence removeCompetence) {
        assert csUm.contains(removeCompetence);

        int removedCIndex = csUm.indexOf(removeCompetence);
        
        cs.remove(removeCompetence);
        removeCompetence.setParent(null);

        emitChildDeleted(removeCompetence, removedCIndex);
    }

    /**
     * Remove action pattern from the plan and notify listeners of plan about
     * removal of a child.
     *
     * @param ap Action pattern to be removed.
     */
    public void removeActionPattern(ActionPattern ap) {
        assert apsUm.contains(ap);

        int removedAPIndex = apsUm.indexOf(ap);
        
        aps.remove(ap);
        ap.setParent(null);

        emitChildDeleted(ap, removedAPIndex);
    }

    /**
     * Synchronize the lap tree to other the lap tree. After all is said and
     * done, we should have two trees that would serialize into a same plan, but
     * don't share any data.
     *
     * @param other The tree we are supposed to synchronize to.
     */
    public void synchronize(PoshPlan other) {
        // First remove everything, combination of two plans could cause cycles)
        ActionPattern[] apArray = aps.toArray(new ActionPattern[aps.size()]);
        for (ActionPattern ap : apArray) {
            removeActionPattern(ap);
        }
        Competence[] compArray = cs.toArray(new Competence[cs.size()]);
        for (Competence c : compArray) {
            removeCompetence(c);
        }
        Sense[] goalSenses = dc.getGoal().toArray(new Sense[0]);
        for (Sense goalSense : goalSenses) {
            dc.getGoal().remove(goalSense);
        }


        // Synchronzie from the other plan
        try {
            // because we can't remove all drives (that would be invalid tree, so 
            // the dummy drive is always added), first we add extra drive with name 
            // that isn't used in this or other drives. Action name is left alone, because
            // there is no AP/C and the sync drive will be removed.
            DriveElement[] originalDrives = dc.getDrives().toArray(new DriveElement[0]);
            List<DriveElement> allDrives = new ArrayList<DriveElement>();
            allDrives.addAll(dc.getDrives());
            allDrives.addAll(other.dc.getDrives());
            
            String syncDriveName = getUnusedName("temporaryDriveSyncName", allDrives);
            DriveElement syncDrive = LapElementsFactory.createDriveElementNoTriggers(syncDriveName);
            dc.addDrive(syncDrive);
            
            for (DriveElement drive : originalDrives) {
                dc.removeDrive(drive);
            }

            
            for (DriveElement drive : other.dc.getDrives()) {
                dc.addDrive(LapElementsFactory.createDriveElement(drive));
            }
            
            dc.removeDrive(syncDrive);
            
            for (ActionPattern ap : other.apsUm) {
                addActionPattern(LapElementsFactory.createActionPattern(ap));
            }
            for (Competence c : other.csUm) {
                addCompetence(LapElementsFactory.createCompetence(c));
            }
            for (Sense oGoalSense : other.dc.getGoal()) {
                dc.getGoal().add(LapElementsFactory.createSense(oGoalSense));
            }
        } catch (DuplicateNameException ex) {
            throw new FubarException("Original tree should be correct thus new one should be too.", ex);
        } catch (CycleException ex) {
            throw new FubarException("Original tree should be correct thus new one should be too.", ex);
        }
    }

    /**
     * Get id of adopt. The is is an index into all adopts. 
     * @param adopt Adopt for which we want index
     * @return found index
     * @throws IllegalArgumentException If adopt is not in the adopts of the
     * plan.
     */
    public int getAdoptId(Adopt adopt) {
        return getElementId(adsUm, adopt);
    }
    
    /**
     * Get id of AP. The is is an index into all APs in the plan. 
     * @param actionPattern AP for which we want index
     * @return found index
     * @throws IllegalArgumentException If AP is not in APs of the plan.
     */
    public int getActionPatternId(ActionPattern actionPattern) {
        return getElementId(apsUm, actionPattern);
    }

    /**
     * Get id of competence. The is is an index into all Cs in the plan. 
     * @param competence C for which we want index
     * @return found index
     * @throws IllegalArgumentException If competence is not in the competences
     * of the plan.
     */
    public int getCompetenceId(Competence competence) {
        return getElementId(csUm, competence);
    }
    
}
