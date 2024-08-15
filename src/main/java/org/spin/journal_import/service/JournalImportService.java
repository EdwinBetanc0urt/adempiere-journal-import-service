/************************************************************************************
 * Copyright (C) 2018-2024 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Edwin Betancourt, EdwinBetanc0urt@outlook.com                    *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.journal_import.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.adempiere.core.domains.models.I_C_ElementValue;
import org.adempiere.core.domains.models.I_GL_Journal;
import org.adempiere.core.domains.models.I_GL_JournalBatch;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MElementValue;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalBatch;
import org.compiere.model.MJournalLine;
import org.compiere.model.Query;
import org.compiere.util.CCache;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.proto.journal_import.CreateJournalRequest;
import org.spin.service.grpc.util.value.NumberManager;
import org.spin.service.grpc.util.value.TimeManager;

import com.google.protobuf.Empty;

public class JournalImportService {

	private static CCache<String, MElementValue> cacheElementValue = new CCache<String, MElementValue>("C_ElementValue", 50, 45);

	public static MJournalBatch validateAndGetJournalBatch(int journalBatchId) {
		if (journalBatchId <= 0) {
			throw new AdempiereException("@FillMandatory@ @GL_JournalBatch_ID@");
		}
		MJournalBatch journalbBatch = new MJournalBatch(Env.getCtx(), journalBatchId, null);

		return journalbBatch;
	}
	public static MJournalBatch validateAndGetJournalBatch(String documentNo) {
		if (Util.isEmpty(documentNo, true)) {
			throw new AdempiereException("@FillMandatory@ @DocumentNo@");
		}
		MJournalBatch journalBatch = new Query(
			Env.getCtx(),
			I_GL_JournalBatch.Table_Name,
			"DocumentNo = ?",
			null
		)
			.setOnlyActiveRecords(true)
			.first()
		;
		if (journalBatch == null) {
			journalBatch = new MJournalBatch(Env.getCtx(), 0, null);
		}

		return journalBatch;
	}


	public static MJournal validateAndGetJournal(int journalId) {
		if (journalId <= 0) {
			throw new AdempiereException("@FillMandatory@ @GL_Journal_ID@");
		}
		MJournal journal = new MJournal(Env.getCtx(), journalId, null);

		return journal;
	}
	public static MJournal validateAndGetJournal(String documentNo) {
		if (Util.isEmpty(documentNo, true)) {
			throw new AdempiereException("@FillMandatory@ @DocumentNo@");
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
		if (journal == null) {
			journal = new MJournal(Env.getCtx(), 0, null);
		}

		return journal;
	}


	public static MElementValue getAccountgValue(String value) {
		MElementValue elementValue = cacheElementValue.get(value);
		if (elementValue != null) {
			return elementValue;
		}

		elementValue = new Query(
			Env.getCtx(),
			I_C_ElementValue.Table_Name,
			"Value = ?",
			null
		)
			.setParameters(value)
			.first()
		;
		cacheElementValue.put(value, elementValue);
		return elementValue;
	}


	public static Empty.Builder createJournal(CreateJournalRequest request) {
		MJournalBatch journalBatch = validateAndGetJournalBatch(
			request.getDocumentNo()
		);
		if (journalBatch.isProcessed()) {
			return Empty.newBuilder();
		}

		SimpleDateFormat[] formatosFecha = new SimpleDateFormat[] {
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"),
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
		};
		Date fechaDate = null;
		for (SimpleDateFormat formato : formatosFecha) {
			try {
				fechaDate = formato.parse(
					request.getDateAcct()
				);
				break;
			} catch (ParseException e) {
				// Ignorar excepción
			}
		}
		if (fechaDate == null) {
			throw new AdempiereException("@FillMandatory@ @DateAcct@");
		}
		Timestamp dateAcct = TimeManager.getTimestampFromDate(
			fechaDate
		);

		MJournal journal = validateAndGetJournal(
			request.getDocumentNo()
		);
		if (journal == null || journal.getGL_Journal_ID() <= 0) {
			journal = new MJournal(Env.getCtx(), 0, null);
		}

		journal.setDateAcct(dateAcct);

		MJournalLine journalLine = new MJournalLine(journal);
		journalLine.setAmtAcctDr(
			NumberManager.getBigDecimalFromString(
				request.getAmtSourceDr()
			)
		);
		journalLine.setAmtAcctCr(
			NumberManager.getBigDecimalFromString(
				request.getAmtSourceCr()
			)
		);

		if (Util.isEmpty(request.getUserElement1Value(), true)) {
			MElementValue elementValue = getAccountgValue(
				request.getUserElement1Value()
			);
			journalLine.setUserElement1_ID(
				elementValue.getC_ElementValue_ID()
			);
		}

		if (Util.isEmpty(request.getUserElement2Value(), true)) {
			MElementValue elementValue = getAccountgValue(
				request.getUserElement2Value()
			);
			journalLine.setUserElement2_ID(
				elementValue.getC_ElementValue_ID()
			);
		}

		return Empty.newBuilder();
	}

}
