//
//  PhoneAuthView.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/30.
//

import SwiftUI

struct PhoneAuthView: View {
    @StateObject var viewModel: PhoneAuthViewModel = DependencyContainer.instance.providePhoneAuthViewModel()
    
    @State private var phoneNumber: String = ""
    
    @State private var otpCode: String = ""
    
    @FocusState private var isOtpFocused: Bool
    
    var onVerified: () -> Void
    
    private var phoneInputSection: some View {
        VStack(spacing: 20) {
            TextField("휴대폰 번호 (예: 01012345678)", text: $phoneNumber)
                .keyboardType(.phonePad)
                .onChange(of: phoneNumber) { newValue in
                    phoneNumber = newValue.filter { $0.isNumber || $0 == "+" }
                        .prefix(20)
                        .description
                }
                .padding()
                .background(RoundedRectangle(cornerRadius: 12).stroke(Color.blue))
            
            Button {
                let formatted = Utils.formatToE164(phoneNumber)
                // 멍청한 AppleDeveloper가 로그인이 안되어 APNs Key 발급이 안되므로 전화번호 인증은 잠시 생략합니다.
                //viewModel.sendOtp(formatted)
                onVerified()
            } label: {
                if viewModel.uiState.isLoading {
                    ProgressView().tint(.white)
                } else {
                    Text("인증번호 받기")
                        .fontWeight(.bold)
                }
            }
            .frame(maxWidth: .infinity, minHeight: 50)
            .background(phoneNumber.count >= 10 ? Color.blue : Color.gray.opacity(0.4))
            .foregroundColor(.white)
            .cornerRadius(12)
            .disabled(phoneNumber.count < 10 || viewModel.uiState.isLoading)
        }
    }
    
    private var otpInputSection: some View {
        VStack(spacing: 24) {
            Button { /* 수정 로직 */ } label: {
                Text("번호가 \(phoneNumber) 맞나요?").font(.caption)
            }
            OtpInputField(
                otpText: $otpCode,
                onOtpCompleted: { code in
                    viewModel.verifyOtp(code)
                }
            )
            Button {
                viewModel.verifyOtp(otpCode)
            } label: {
                if viewModel.uiState.isLoading {
                    ProgressView().tint(.white)
                } else {
                    Text("인증하기").fontWeight(.bold)
                }
            }
            .frame(maxWidth: .infinity, minHeight: 50)
            .background(otpCode.count == 6 ? Color.blue : Color.gray.opacity(0.4))
            .cornerRadius(12)
            .foregroundColor(.white)
            .disabled(otpCode.count != 6 || viewModel.uiState.isLoading)
        }
    }
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                Text(viewModel.uiState.isCodeSent ? "인증번호 입력" : "휴대폰 인증")
                    .font(.title)
                    .bold()
                    .foregroundColor(.blue)
                Text(viewModel.uiState.isCodeSent
                     ? "문자로 전송된 6자리 코드를 입력해주세요."
                     : "안전한 사용을 위해 휴대폰 번호를 입력해주세요."
                )
                .foregroundColor(.gray)
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
                Spacer().frame(height: 30)
                if !viewModel.uiState.isCodeSent {
                    phoneInputSection
                } else {
                    otpInputSection
                }
                if let error = viewModel.uiState.errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .font(.footnote)
                        .padding(.top, 10)
                }
            }
            .padding(24)
        }
        .onReceive(viewModel.$uiState.map { $0.isVerified }.removeDuplicates()) { verified in
            if verified {
                onVerified()
            }
        }
    }
}

struct OtpInputField: View {
    @Binding var otpText: String
    
    var onOtpCompleted: (String) -> Void

    let boxSize: CGFloat = 45

    var body: some View {
        HStack(spacing: 8) {
            ForEach(0..<6, id: \.self) { index in
                ZStack {
                    let char = index < otpText.count
                    ? String(otpText[otpText.index(otpText.startIndex, offsetBy: index)])
                    : ""
                    let isFocused = otpText.count == index

                    RoundedRectangle(cornerRadius: 8)
                        .stroke(isFocused ? Color.blue : Color.gray.opacity(0.3), lineWidth: isFocused ? 2 : 1)
                        .background(
                            RoundedRectangle(cornerRadius: 8)
                                .fill(char.isEmpty ? Color.clear : Color.blue.opacity(0.1))
                        )
                        .frame(width: boxSize, height: 50)
                    Text(char)
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(char.isEmpty ? .black : .blue)
                }
            }
        }
        .overlay(
            TextField("", text: Binding(
                get: { otpText },
                set: { newValue in
                    let filtered = newValue.filter { $0.isNumber }.prefix(6)
                    otpText = String(filtered)

                    if otpText.count == 6 {
                        onOtpCompleted(otpText)
                    }
                }
            ))
            .keyboardType(.numberPad)
            .textContentType(.oneTimeCode)
            .foregroundColor(.clear)
            .accentColor(.clear)
            .frame(width: 0, height: 0)
            .opacity(0)
        )
    }
}

struct PhoneAuthView_Previews: PreviewProvider {
    static var previews: some View {
        PhoneAuthView(onVerified: {})
    }
}
