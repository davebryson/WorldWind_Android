/*
 * Copyright (C) 2012 DreamHammer.com
 */

package gov.nasa.worldwind.kml;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.xml.*;

import java.util.*;

/**
 * Represents the KML <i>Update</i> element and provides access to its contents.
 *
 * @author tag
 * @version $Id: KMLUpdate.java 771 2012-09-14 19:30:10Z tgaskins $
 */
public class KMLUpdate extends KMLAbstractObject
{
    protected List<KMLUpdateOperation> operations; // operations are performed in the order specified in the KML file
    protected boolean updatesApplied;

    /**
     * Construct an instance.
     *
     * @param namespaceURI the qualifying namespace URI. May be null to indicate no namespace qualification.
     */
    public KMLUpdate(String namespaceURI)
    {
        super(namespaceURI);
    }

    @Override
    protected void doAddEventContent(Object o, XMLEventParserContext ctx, XMLEvent event, Object... args)
    {
        if (o instanceof KMLChange)
            this.addChange((KMLChange) o);
        else if (o instanceof KMLCreate)
            this.addCreate((KMLCreate) o);
        else if (o instanceof KMLDelete)
            this.addDelete((KMLDelete) o);
        else
            super.doAddEventContent(o, ctx, event, args);
    }

    public String getTargetHref()
    {
        return (String) this.getField("targetHref");
    }

    protected void addChange(KMLChange o)
    {
        if (this.operations == null)
            this.operations = new ArrayList<KMLUpdateOperation>();

        this.operations.add(o);
    }

    protected void addCreate(KMLCreate o)
    {
        if (this.operations == null)
            this.operations = new ArrayList<KMLUpdateOperation>();

        this.operations.add(o);
    }

    protected void addDelete(KMLDelete o)
    {
        if (this.operations == null)
            this.operations = new ArrayList<KMLUpdateOperation>();

        this.operations.add(o);
    }

    public boolean isUpdatesApplied()
    {
        return updatesApplied;
    }

    public void applyOperations()
    {
        this.updatesApplied = true;

        if (WWUtil.isEmpty(this.getTargetHref()))
            return;

        if (this.operations == null || this.operations.size() == 0)
            return;

        Object o = this.getRoot().resolveReference(this.getTargetHref());

        if (o == null || !(o instanceof KMLRoot))
            return;

        KMLRoot targetRoot = (KMLRoot) o;

        for (KMLUpdateOperation operation : this.operations)
        {
            operation.applyOperation(targetRoot);
        }
        targetRoot.firePropertyChange(AVKey.UPDATED, null, this);
    }
}
