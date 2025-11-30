//
//  InfoSetupView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import SwiftUI

struct InfoSetupView: View {
    @ObservedObject var viewModel: ProfileSetupViewModel
    
    @Environment(\.presentationMode) var presentationMode

    @State private var showDatePicker = false
    
    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy년 MM월 dd일"
        return formatter
    }()
    
    let onSetupComplete: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text("나에 대한 매력적인 정보를 알려주세요.")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                    .padding(.top, 16)
                VStack(alignment: .leading, spacing: 4) {
                    GeneralTextField(
                        label: "Full Name (필수)",
                        value: viewModel.uiState.name,
                        onValueChange: viewModel.onNameChange,
                        submitLabel: .next
                    )
                    if let error = viewModel.uiState.nameError {
                        Text(error).foregroundColor(.red).font(.caption)
                    }
                }
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        Text(viewModel.uiState.birthdayMillis != nil ?
                             dateFormatter.string(from: Date(timeIntervalSince1970: TimeInterval(viewModel.uiState.birthdayMillis! / 1000)))
                             : "생년월일 (필수)")
                        .foregroundColor(viewModel.uiState.birthdayMillis != nil ? .primary : .gray)
                        Spacer()
                        Image(systemName: "calendar")
                            .onTapGesture { showDatePicker = true }
                    }
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(viewModel.uiState.birthdayError != nil ? Color.red : Color.gray)
                    )
                    if let error = viewModel.uiState.birthdayError {
                        Text(error).foregroundColor(.red).font(.caption)
                    }
                }
                .onTapGesture { showDatePicker = true }
                VStack(alignment: .leading, spacing: 4) {
                    TextEditor(text: $viewModel.uiState.bio)
                        .frame(height: 120)
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(Color.gray)
                        )
                }
                VStack(alignment: .leading, spacing: 4) {
                    GeneralTextField(
                        label: "직업 (필수)",
                        value: viewModel.uiState.job,
                        onValueChange: viewModel.onJobChange,
                        submitLabel: .done
                    )
                    if let error = viewModel.uiState.jobError {
                        Text(error).foregroundColor(.red).font(.caption)
                    }
                }
                Button(action: {
                    viewModel.completeProfileSetup()
                }) {
                    ZStack {
                        if viewModel.uiState.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle())
                        } else {
                            Text("프로필 설정 완료")
                                .foregroundColor(.white)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(viewModel.uiState.isSubmitEnabled ? Color.blue : Color.gray)
                    .cornerRadius(16)
                }
                .disabled(!viewModel.uiState.isSubmitEnabled || viewModel.uiState.isLoading)
                .padding(.vertical, 20)
                
                Spacer()
            }
            .padding(.horizontal, 24)
        }
        .navigationTitle("프로필 정보 입력 (3/3)")
        .sheet(isPresented: $showDatePicker) {
            BirthdayPickerView { selectedDate in
                viewModel.onBirthdaySelected(selectedDate)
            }
        }
        .onChange(of: viewModel.uiState.isSetupComplete) { completed in
            if completed {
                viewModel.consumeSetupCompleteEvent()
                onSetupComplete()
            }
        }
    }
}

struct BirthdayPickerView: View {
    @Environment(\.presentationMode) var presentationMode
    
    @State private var selectedDate = Calendar.current.date(byAdding: .year, value: -20, to: Date()) ?? Date()
    
    var onSelected: (Int64) -> Void
    
    var body: some View {
        VStack {
            DatePicker(
                "생년월일 선택",
                selection: $selectedDate,
                displayedComponents: .date
            )
            .datePickerStyle(GraphicalDatePickerStyle())
            .padding()
            Button("확인") {
                onSelected(Int64(selectedDate.timeIntervalSince1970 * 1000))
                presentationMode.wrappedValue.dismiss()
            }
            .padding()
        }
    }
}

struct InfoSetupView_Previews: PreviewProvider {
    static var previews: some View {
        InfoSetupView(viewModel: DependencyContainer.instance.provideProfileSetupViewModel(), onSetupComplete: {})
    }
}
