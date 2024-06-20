package org.spin.journal_import.service;

import org.adempiere.core.domains.models.I_GL_Journal;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MJournal;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.proto.journal_import.CreateJournalRequest;
import org.spin.proto.journal_import.ProcessJournalRequest;

import com.google.protobuf.Empty;

public class JournalImportService {

	public static MJournal validateAndGetJournal(int journalId) {
		if (journalId <= 0) {
			throw new AdempiereException("@FillMandatory@ @GL_Journal_ID@");
		}
		MJournal journal = new MJournal(Env.getCtx(), journalId, null);

		return journal;
	}

	public static MJournal validateAndGetJournal(String documentNo) {
		if (Util.isEmpty(documentNo, true)) {
			throw new AdempiereException("@FillMandatory@ @GL_Journal_ID@");
		}
		MJournal journal = new Query(
			Env.getCtx(),
			I_GL_Journal.Table_Name,
			"DocumentNo = ?",
			null
		)
			.setOnlyActiveRecords(true)
			.first()
		;

		return journal;
	}


	public static Empty.Builder createJournal(CreateJournalRequest request) {
		// MJournal journal = validateAndGetJournal(
		// 	request.getCodcia()
		// );
		// if (journal != null && journal.isProcessed()) {
		// 	throw new AdempiereException(
		// 		"@GL_Journal_ID@ <" + journal.getDocumentNo() + "> @Processed@"
		// 	);
		// }

		return Empty.newBuilder();
	}


	public static Empty.Builder processJournal(ProcessJournalRequest request) {
		// MJournal journal = validateAndGetJournal(
		// 	request.getCodcia()
		// );
		// if (journal != null && journal.isProcessed()) {
		// 	throw new AdempiereException(
		// 		"@GL_Journal_ID@ <" + journal.getDocumentNo() + "> @Processed@"
		// 	);
		// }

		return Empty.newBuilder();
	}

}
