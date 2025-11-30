//
//  GenderSetupView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/26.
//

import SwiftUI

struct GenderSetupView: View {
    @ObservedObject var viewModel: ProfileSetupViewModel
    
    var onNext: () -> Void
    
    var body: some View {
        VStack {
            Spacer().frame(height: 40)
            VStack(spacing: 20) {
                Text("1/3. 나의 성별은 무엇인가요?")
                    .font(.title)
                    .bold()
                    .padding(.bottom, 60)
                GenderSelector(
                    selectedGender: viewModel.uiState.selectedGender,
                    onGenderSelected: viewModel.onGenderSelected
                )
            }
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 24)
            Spacer()
            Button(action: onNext) {
                Text("다음")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity, minHeight: 52)
                    .background(viewModel.uiState.selectedGender != nil ? Color.blue : Color.gray)
                    .cornerRadius(16)
            }
            .disabled(viewModel.uiState.selectedGender == nil)
            .padding(24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct GenderSetupView_Previews: PreviewProvider {
    static var previews: some View {
        GenderSetupView(viewModel: DependencyContainer.instance.provideProfileSetupViewModel(), onNext: {})
    }
}
