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
package org.spin.journal_import.controller;

import org.compiere.util.CLogger;
import org.spin.journal_import.service.JournalImportService;
import org.spin.proto.journal_import.CreateJournalRequest;

import com.google.protobuf.Empty;

import org.spin.proto.journal_import.JournalImportServiceGrpc.JournalImportServiceImplBase;
import org.spin.proto.journal_import.ProcessJournalRequest;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 * Service for backend of Journal Import
 */
public class JournalImport extends JournalImportServiceImplBase {

	/**	Logger			*/
	private CLogger log = CLogger.getCLogger(JournalImport.class);


	@Override
	public void createJournal(CreateJournalRequest request, StreamObserver<Empty> responseObserver) {
		try {
			log.fine("CreateJournal: " + request);
			Empty.Builder builder = JournalImportService.createJournal(request);
			responseObserver.onNext(
				builder.build()
			);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			e.printStackTrace();
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}


	@Override
	public void processJournal(ProcessJournalRequest request, StreamObserver<Empty> responseObserver) {
		try {
			log.fine("CreateJournal: " + request);
			Empty.Builder builder = JournalImportService.processJournal(request);
			responseObserver.onNext(
				builder.build()
			);
			responseObserver.onCompleted();
		} catch (Exception e) {
			log.severe(e.getLocalizedMessage());
			e.printStackTrace();
			responseObserver.onError(
				Status.INTERNAL
					.withDescription(e.getLocalizedMessage())
					.withCause(e)
					.asRuntimeException()
			);
		}
	}

}
